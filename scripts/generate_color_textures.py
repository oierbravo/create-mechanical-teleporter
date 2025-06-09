from PIL import Image, ImageColor
import os, shutil

paths = {
    "block": "output/block/hand_teleporter/",
}

for path in paths:
    shutil.rmtree(paths[path])
    #if not os.path.exists(paths[path]):
    os.makedirs(paths[path])


colors = {
    "black":"#201b2b",
    "blue":"#003dff",
    "brown":"#954700",
    "cyan":"#0df4e6",
    "gray":"#727272",
    "green":"#0d3733",
    "light_blue":"#afc2ff",
    "light_gray":"#bdbdbd",
    "lime":"#94ff00",
    "magenta":"#ff00f8",
    "orange":"#ff9f00",
    "pink":"#ffa9ee",
    "purple":"#ff00cb",
    "red":"#b50000",
    "white":"#f9f9f9",
    "yellow":"#fffe00"
}

os

for color in colors:
    rgb = colors[color]
    item_base = Image.open('template/base_item.png').convert('RGBA')
    item_tint = Image.open('template/base_item_tint.png').convert('RGBA')



    #create the coloured overlays
    itemColorOverlay = Image.new('RGB',item_base.size,ImageColor.getrgb(rgb))

    #create a mask using RGBA to define an alpha channel to make the overlay transparent
    itemTintMaskAll = Image.new('RGBA',item_tint.size,(0,0,0,123))
    itemMaskAll = Image.new('RGBA',item_tint.size,(123,123,123,0))
    itemTintMask = Image.open('template/base_item_tint_mask.png').convert('L')
    itemMask = Image.open('template/base_item_mask.png').convert('RGBA')

    itemTintImageColored = Image.composite(item_tint,itemColorOverlay,itemTintMaskAll).convert('RGBA')
    itemTintImage = item_tint.copy()
    itemTintImage.paste(itemTintImageColored, None, itemTintMask)

    itemImage = item_base.copy()
    itemImage.paste(itemTintImage, (0,0),itemTintMask)
    itemImage.save(paths['block'] + '%s.png' % (color,), optimize=True)

