import sys
from pathlib import Path
import csv

TARGET = 'http_req_duration'
RESULT_FILE_NAME = 'result.csv'

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
            reader = csv.reader(f)
            # metric_name,timestamp,metric_value,check,error,error_code,group,method,name,proto,scenario,status,subproto,tls_version,url,extra_tags
            for row in reader:
                if row[0] == TARGET:
                    metric_value = float(row[2])

                    durationDict[fileName] += metric_value
                    countDict[fileName] += 1
                    break
    
    for key in durationDict:
        durationDict[key] = durationDict[key] / countDict[key]
    return durationDict
    
def writeResultFile(p: Path, result: dict):
    with p.open('w', newline='') as f:
        writer = csv.writer(f)

        headerFlag = True
        for i, key in enumerate(result):
            lock, backoff, backoffValue, retry, retryValue, ticket, ticketValue, vus, vusValue, waitTime, waitTimeValue, leaseTime, leaseTimeValue = key.split('_')
            if headerFlag:
                writer.writerow(['id', 'lock', 'backoff', 'retry', 'ticket', 'vus', 'waitTime', 'leasTime', 'duration'])
                headerFlag = False
            
            writer.writerow([i, lock, backoffValue, retryValue, ticketValue, vusValue, waitTimeValue, leaseTimeValue, result[key]])

if __name__ == '__main__':
    validateDir('./result')

    assert len(sys.argv) > 1, 'iteration을 지정할 인자가 필요합니다.'

    path = '/'.join(['./result', sys.argv[1]])
    validateDir(path)
    p = Path(path)
    print(p)

    result = makeResult(p)
    
    path = '/'.join(['./result', sys.argv[1], 'result.csv'])
    p = Path(path)
    writeResultFile(p, result)
