import sys
from pathlib import Path
import pandas as pd

TARGET = 'http_req_duration'
RESULT_FILE_NAME = 'result.csv'
SUPPORTED_EXTENSION = set(['json', 'csv'])

def init():
    p = Path('./result')

    if p.exists():
        return
    
    p.mkdir()

def validateDir(path: str):
    p = Path(path)
    assert p.exists() and p.is_dir(), '경로에 디렉토리가 존재하지 않습니다.'
    
def getNameWithoutIteration(fileName: str):
    name = fileName.split('.')[0]

    return '_'.join(name.split('_')[:-1])

def validateFileName(fileName: str):
    nameList = fileName.split('_')
    if len(nameList) != 16:
        return False
    
    if fileName.split('.')[-1] not in SUPPORTED_EXTENSION:
        return False

    return True

def makeResult(p: Path):
    durationDict = dict()
    failCountDict = dict()
    countDict = dict()

    for x in p.iterdir():
        if not x.is_file():
            continue

        if x.name == RESULT_FILE_NAME:
            continue

        if not validateFileName(x.name):
            continue

        fileName = getNameWithoutIteration(x.name)

        if fileName not in durationDict:
            durationDict[fileName] = 0
            failCountDict[fileName] = [0, 0]
            countDict[fileName] = 0

        with x.open('r', newline='') as f:
            df = pd.read_json(f, encoding='utf-8')

            # metric_name,timestamp,metric_value,check,error,error_code,group,method,name,proto,scenario,status,subproto,tls_version,url,extra_tags
            durationDict[fileName] += df['metrics']['http_req_duration']['values']['avg']
            failCountDict[fileName][0] += df['metrics']['http_req_failed']['values']['passes']
            failCountDict[fileName][1] += df['metrics']['http_req_failed']['values']['fails']
            countDict[fileName] += 1

    for key in durationDict:
        durationDict[key] = durationDict[key] / countDict[key]
        failCountDict[fileName][0] = failCountDict[fileName][0] / countDict[key]
        failCountDict[fileName][1] = failCountDict[fileName][1] / countDict[key]
    return durationDict, failCountDict

def writeResultFile(p: Path, durationResult: dict, failResult: dict):
    with p.open('w', newline='') as f:
        columns = ['lock', 'vus', 'tickets', 'minBackoff', 'maxBackoff', 'retry', 'waitTime', 'leaseTime', 'duration', 'passes', 'fails']
        df = pd.DataFrame(columns=columns)

        for i, key in enumerate(durationResult):
            lock, vus, vusValue, tickets, ticketsValue, minBackoff, minBackoffValue, maxBackoff, maxBackoffValue, retry, retryValue, waitTime, waitTimeValue, leaseTime, leaseTimeValue = key.split('_')

            se = pd.Series([lock, vusValue, ticketsValue, minBackoffValue, maxBackoffValue, retryValue, waitTimeValue, leaseTimeValue, durationResult[key], failResult[key][0], failResult[key][1]], index=df.columns)
            df = pd.concat([df, pd.DataFrame([se])], ignore_index=True)
        df.to_csv(f)

if __name__ == '__main__':
    init()
    validateDir('./result')

    assert len(sys.argv) > 1, 'iteration을 지정할 인자가 필요합니다.'

    # path = '/'.join(['./result', sys.argv[1]])
    path = './output'
    validateDir(path)
    p = Path(path)

    result, failResult = makeResult(p)
    path = '/'.join(['./result', 'result_' + sys.argv[1] + '.csv'])
    p = Path(path)
    writeResultFile(p, result, failResult)
