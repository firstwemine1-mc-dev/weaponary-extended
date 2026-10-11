"""Copy the status effect icons from ../pixel-art into the mod.

The art is 16x16 (pixel-art/weaponry/docs/rule.md); the game draws effect icons at 18x18, so each
icon gets a 1 px transparent border on the way in, instead of being stretched.

    python scripts/copy_effect_icons.py

Reads  ../pixel-art/weaponry/png/effects/<name>.png
Writes src/main/resources/assets/weaponry/textures/mob_effect/<name>.png
Standard library only.
"""
import struct
import zlib
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SRC = ROOT.parent / 'pixel-art' / 'weaponry' / 'png' / 'effects'
DST = ROOT / 'src' / 'main' / 'resources' / 'assets' / 'weaponry' / 'textures' / 'mob_effect'
ART_SIZE, ICON_SIZE = 16, 18
PAD = (ICON_SIZE - ART_SIZE) // 2


def read_rgba(path):
    data = path.read_bytes()
    pos, idat, header = 8, b'', None
    while pos < len(data):
        length, tag = struct.unpack('>I4s', data[pos:pos + 8])
        body = data[pos + 8:pos + 8 + length]
        if tag == b'IHDR':
            header = struct.unpack('>IIBBBBB', body)
        elif tag == b'IDAT':
            idat += body
        pos += 12 + length
    width, height, depth, color, _, _, interlace = header
    if (width, height) != (ART_SIZE, ART_SIZE) or depth != 8 or color != 6 or interlace:
        raise SystemExit(f'{path.name}: expected a {ART_SIZE}x{ART_SIZE} 8-bit RGBA PNG')
    raw, stride, rows, prev = zlib.decompress(idat), width * 4, [], bytearray(width * 4)
    for y in range(height):
        kind, line = raw[y * (stride + 1)], bytearray(raw[y * (stride + 1) + 1:(y + 1) * (stride + 1)])
        for i in range(stride):
            a = line[i - 4] if i >= 4 else 0
            b = prev[i]
            c = prev[i - 4] if i >= 4 else 0
            if kind == 1:
                line[i] = (line[i] + a) & 255
            elif kind == 2:
                line[i] = (line[i] + b) & 255
            elif kind == 3:
                line[i] = (line[i] + (a + b) // 2) & 255
            elif kind == 4:
                p = a + b - c
                pa, pb, pc = abs(p - a), abs(p - b), abs(p - c)
                line[i] = (line[i] + (a if pa <= pb and pa <= pc else b if pb <= pc else c)) & 255
        rows.append(bytes(line))
        prev = line
    return rows


def write_rgba(path, rows, size):
    def chunk(tag, body):
        return struct.pack('>I', len(body)) + tag + body + struct.pack('>I', zlib.crc32(tag + body))
    raw = b''.join(b'\0' + row for row in rows)
    path.write_bytes(b'\x89PNG\r\n\x1a\n'
                     + chunk(b'IHDR', struct.pack('>IIBBBBB', size, size, 8, 6, 0, 0, 0))
                     + chunk(b'IDAT', zlib.compress(raw, 9))
                     + chunk(b'IEND', b''))


def main():
    DST.mkdir(parents=True, exist_ok=True)
    empty = bytes(ICON_SIZE * 4)
    for src in sorted(SRC.glob('*.png')):
        art = read_rgba(src)
        rows = [empty] * PAD + [bytes(PAD * 4) + row + bytes(PAD * 4) for row in art] + [empty] * PAD
        write_rgba(DST / src.name, rows, ICON_SIZE)
        print(f'{src.name}: {ART_SIZE}x{ART_SIZE} -> {ICON_SIZE}x{ICON_SIZE}')


if __name__ == '__main__':
    main()
