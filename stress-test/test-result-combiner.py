import sys
from pathlib import Path
import pandas as pd

TARGET = 'http_req_duration'
RESULT_FILE_NAME = 'result.csv'

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

def makeResult(p: Path):
    durationDict = dict()
    countDict = dict()

    for x in p.iterdir():
        if not x.is_file():
            continue
        
        if x.name == RESULT_FILE_NAME:
            continue

        fileName = getNameWithoutIteration(x.name)

        if fileName not in durationDict:
            durationDict[fileName] = 0
            countDict[fileName] = 0
        
        with x.open('r', newline='') as f:
            df = pd.read_json(f)

            # metric_name,timestamp,metric_value,check,error,error_code,group,method,name,proto,scenario,status,subproto,tls_version,url,extra_tags
            durationDict[fileName] += df['metrics']['http_req_duration']['values']['avg']
            countDict[fileName] += 1

    for key in durationDict:
        durationDict[key] = durationDict[key] / countDict[key]
    return durationDict
    
def writeResultFile(p: Path, result: dict):
    with p.open('w', newline='') as f:
        columns = ['lock', 'vus', 'tickets', 'backoff', 'retry', 'waitTime', 'leaseTime', 'duration']
        df = pd.DataFrame(columns=columns)

        for i, key in enumerate(result):
            lock, vus, vusValue, tickets, ticketsValue, backoff, backoffValue, retry, retryValue, waitTime, waitTimeValue, leaseTime, leaseTimeValue = key.split('_')

            se = pd.Series([lock, vusValue, ticketsValue, backoffValue, retryValue, waitTimeValue, leaseTimeValue, result[key]], index=df.columns)
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

    result = makeResult(p)
    
    path = '/'.join(['./result', sys.argv[1], 'result.csv'])
    p = Path(path)
    writeResultFile(p, result)
