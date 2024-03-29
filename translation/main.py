import xml.etree.ElementTree as ET
import os
import json

def get_strings_count(filename):
    count = 0
    remove_count = 0
    main_strings_xml = ET.parse(filename).getroot()
    for strings in main_strings_xml.iter('string'):
        for _ in strings.attrib:
            if _ == 'translatable':
                remove_count = remove_count + 1
        count = count + 1
    for items in main_strings_xml.iter('item'):
        count = count + 1
    return count-remove_count

def load_json(filename):
    with open(filename) as json_file:
        return json.load(json_file)

total_strings = get_strings_count('strings.xml')

os.system("rm -rf tmp; unzip *.zip -d tmp > /dev/null")
filename_mapping = load_json('file_mapping.json')
name_mapping = load_json('name_mapping.json') 
os.system("rm -rf app/ && mkdir app")

fout = open("report.md","w")
count1 = 0
count2 = 0
count3 = 0
for _ in filename_mapping:
    os.system("cp -r tmp/"+filename_mapping[_]+" app/"+_+"")
    com_count = int((get_strings_count("app/"+_+"/strings.xml")/total_strings)*100)
    status = ""
    if com_count == 0:
        status = "❌"
        count1 += 1
    elif com_count == 100:
        status = "✅"
        count3 += 1
    else:
        status = "🚧"
        count2 += 1
    fout.write("|{:^8}|{:^11}|{:^21}|{:^12}|\n".format(status,_.replace("values-",""),name_mapping[_],str(com_count)+"%"))
fout.close()
os.system("rm -rf tmp/")
print("Total: "+str(len(filename_mapping)))
print("❌ = "+str(count1))
print("🚧 = "+str(count2))
print("✅ = "+str(count3))