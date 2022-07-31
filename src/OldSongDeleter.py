import os
import shutil
import hashlib
import time

import pip
try:
    import requests
    import json
except:
    pip.main(['install','json'])
    pip.main(['install','requests'])
    import json
    import requests
headers = {
"accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
"accept-language": "en-US,en;q=0.9",
"cache-control": "max-age=0",
"dnt": "1",
"if-none-match": "W/\"5bb-1w7PpBYUYHeYpyYe6WV2MPmiJj0\"",
"sec-fetch-dest": "document",
"sec-fetch-mode": "navigate",
"sec-fetch-site": "none",
"sec-fetch-user": "?1",
"upgrade-insecure-requests": "1",
"user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.104 Safari/537.36"
}

# Process of creating hash of a sample song
# info = open(".\\1ea5b (COKE FOAM - razy)\\info.dat", "rb")
# expert = open(".\\1ea5b (COKE FOAM - razy)\\ExpertStandard.dat", "rb")
# eplus = open(".\\1ea5b (COKE FOAM - razy)\\ExpertPlusStandard.dat", "rb")
# ibyte = info.read()
# ebyte = expert.read()
# epbyte = eplus.read()
# tot = ibyte+ebyte+epbyte
# print(hashlib.sha1(tot).hexdigest())


def getHashOfMap(srcPath):
    info = open(srcPath+'\\info.dat', 'r')
    infostring = info.read()
    data = json.loads(infostring)
    info.close()
    currf = open(srcPath+'\\info.dat', 'rb')
    currsum = currf.read()
    currf.close()
    for difftype in data["_difficultyBeatmapSets"]:
        for diff in difftype["_difficultyBeatmaps"]:
            currf = open(srcPath+'\\'+diff["_beatmapFilename"],'rb')
            currsum += currf.read()
            currf.close()
    return hashlib.sha1(currsum).hexdigest()


# Gets the song BeatSaver ID from its hash
def getIdFromHash(bshash):
    response = requests.get("https://beatsaver.com/api/maps/hash/" + bshash, headers=headers)
    rawData = json.loads(response.text)
    try:
        return rawData["id"]
    except KeyError:
        return "ERROR"

# Used in main to get the hash of a unformatted folder and rename it
def getHashAndRename(songFolderName):
    hash = getHashOfMap(songFolderName)
    songId = getIdFromHash(hash)
    time.sleep(.16)
    if songId == "ERROR":
        moveMapToDeletedFolder(songFolderName)
        return
    log.write('Renamed: ' + songFolderName + '\n')
    os.rename(songFolderName, songId + " (" + songFolderName + ")")
    songFolderName = songId + " (" + songFolderName + ")"
    if int(songId, 16) < DELETENUM:
        moveMapToDeletedFolder(songFolderName)

# if the song is too old or the songId is not accessible, this method is called to move it to the DELETEDMAPS folder
def moveMapToDeletedFolder(songFolderName):
    if os.path.exists('DELETEDMAPS\\' + songFolderName):
        shutil.rmtree('DELETEDMAPS\\' + songFolderName)
    shutil.move(songFolderName, 'DELETEDMAPS\\' + songFolderName)
    log.write('Deleted: '+songFolderName+'\n')


# oldest song id you are willing to keep
DELETENUM = int('29a8', 16)
songFolderList = os.listdir()
if not os.path.exists('DELETEDMAPS'):
    os.mkdir('DELETEDMAPS')
log = open('DELETEDMAPS\\log.txt', 'w+')
for songFolderName in songFolderList:
    # if statement makes sure program does not delete itself and the DELETEDMAPS folder
    if os.path.isdir(songFolderName) and songFolderName not in 'DELETEDMAPS':
        if ' (' in songFolderName:
            try:
                songId = int(songFolderName[:songFolderName.index(' (')], 16)
                if songId < DELETENUM:
                    moveMapToDeletedFolder(songFolderName)
            except ValueError:
                getHashAndRename(songFolderName)
        else:
            getHashAndRename(songFolderName)
