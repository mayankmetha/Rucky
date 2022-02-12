import os
import json

os.system("rm -rf tmp; unzip *.zip -d tmp > /dev/null")
mapping = {}
with open('mapping.json') as json_file:
    mapping = json.load(json_file)

os.system("rm -rd app; mkdir app")

for _ in mapping:
    os.system("cp -r tmp/"+mapping[_]+" app/"+_+"")
    os.system("rm -rf tmp/"+mapping[_])
    print("Created: "+_)

print("Total: "+str(len(mapping))+"/228")