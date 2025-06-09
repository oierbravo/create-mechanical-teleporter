import os, shutil

baseOrigin = "output/"
baseDestination = "../src/main/resources/assets/create_mechanical_teleporter/textures/"

paths = ["block/hand_teleporter"]

for path in paths:
    if not os.path.exists(baseDestination + path):
        os.makedirs(baseDestination + path)
    shutil.copytree(baseOrigin + path, baseDestination + path, dirs_exist_ok=True)
