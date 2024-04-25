import {uuidv4} from "https://jslib.k6.io/k6-utils/1.4.0/index.js";
import http from "k6/http";

import {check} from "k6";

const setupVar = {
    userNum: __ENV.VSR || 10, // 구매자 수 (vUser),
    ticketStock: __ENV.TICKETS || 50, // 티켓 재고
    backoff: __ENV.BACKOFF || 0,
    retry: __ENV.RETRY || 0,
    waitTime: __ENV.WAITTIME || 0,
    leaseTime: __ENV.LEASETIME || 0,
    iteration: __ENV.ITERATION || 1,
    lockType: __ENV.LOCKTYPE || 'p-lock',
    ticketPrice: 1000,
    setupTimeout: "120s",
};

export const options = {

    setupTimeout: setupVar.setupTimeout,
    scenarios: {
        contacts: {
            executor: "per-vu-iterations",
            vus: setupVar.userNum,
            iterations: 1,
        },
    },
};

const commonHeader = {"Content-Type": "application/json"};

const domain = "http://host.docker.internal:4000";

const commonPwd = "1q2w3e4r@@Q";

export function setup() {
    // 멤버 생성 (1: seller, n: buyer)

    const buyerEmailList = Array.from({length: setupVar.userNum}, () =>
        `test-buyer-${uuidv4()}@test.com`
    );


    const sellerEmail = `test-seller-${uuidv4()}@test.com`;

    wrapWithTimeLogging("유저 생성", () => {
        createUsers({
            emailList: [...buyerEmailList, sellerEmail],
        });
    });

    // 티케팅 생성
    const now = new Date();
    const saleStart = new Date(
        now.getFullYear(),
        now.getMonth(),
        now.getDate(),
        now.getHours() + 9,
        now.getMinutes(),
        now.getSeconds() + 5
    );

    const ticketingId = wrapWithTimeLogging("티케팅 생성", () => {
        return createTicketing({
            title: "Stress Test",
            location: "서울",
            category: "IT",
            runningMinutes: 100,
            price: setupVar.ticketPrice,
            saleStart,
            saleEnd: new Date(
                new Date().setFullYear(now.getFullYear() + 1)
            ).toISOString(),
            eventTime: new Date(
                new Date().setFullYear(now.getFullYear() + 2)
            ).toISOString(),
            stock: setupVar.ticketStock,
            email: sellerEmail,
        });
    });

    return {buyerEmailList, ticketingId};
}

export default function ({buyerEmailList, ticketingId}) {
    const userCounter = __VU;

    const email = buyerEmailList[userCounter - 1];


    // TODO: 락 방법론 마다 분리된 EP 잘 찔러보기
    purchase(ticketingId, 1, email, setupVar.lockType);
}

function createUsers(users) {
    const response = http.post(domain + "/api/members", JSON.stringify(users), {
        headers: commonHeader,
    });

    check(response, {
        "status check after create user": (r) => r.status === 200,
    });
}

function createTicketing(ticketingMetadata) {
    const response = http.post(
        domain + "/api/ticketings",
        JSON.stringify(ticketingMetadata),
        {
            headers: commonHeader,
        }
    );


    check(response, {
        "status check after create ticketing": (r) => r.status === 201,
        "ticketing id check after create ticketing": (r) => {
            const ticketingId = r.json().data["ticketingId"];
            return typeof ticketingId === "string" && ticketingId !== "";
        },
    });

    return response.json("data")["ticketingId"];
}

function purchase(ticketingId, count, buyerEmail, lockType) {

    let postBody = commonPostBody(ticketingId, count, buyerEmail);
    switch (lockType) {
        case 'o-lock' :
            Object.assign(postBody, oLockPostBody());
            break;
        case 'd-lock' :
            Object.assign(postBody, dLockPostBody());
            break;
        case 'p-lock':
            break;
    }

    const response = http.post(
        domain + `/api/purchases/${lockType}`,
        JSON.stringify(postBody),
        {
            headers: commonHeader,
        }
    );

    check(response, {
        "status check after create purchase": (r) => r.status === 201,
    });
}

function commonPostBody(ticketingId, count, buyerEmail) {
    return {
        ticketingId,
        count,
        email: buyerEmail
    }
}

function oLockPostBody() {
    return {
        maxAttempts: setupVar.retry,
        backoff: setupVar.backoff
    }
}

function dLockPostBody() {
    return {
        waitTime: setupVar.waitTime,
        leaseTime: setupVar.leaseTime
    }
}

function pLockPostBody() {
    return {}
}


function wrapWithTimeLogging(tag, callback) {
    const start = new Date();
    const result = callback();
    const end = new Date();
    console.log(`${tag}: 소요시간 - ${end.getTime() - start.getTime()}ms`);
    return result;
}

export function handleSummary(data) {

    const filenameParts = [
        setupVar.lockType,
        `vus_${setupVar.userNum}`,
        `tickets_${setupVar.ticketStock}`,
        `backoff_${setupVar.backoff}`,
        `retry_${setupVar.retry}`,
        `waitTime_${setupVar.waitTime}`,
        `leaseTime_${setupVar.leaseTime}`,
        `${setupVar.iteration}`
    ];

    const filename = `/output/${filenameParts.join('_')}.json`;

    return {
        [filename]: JSON.stringify(data)
    };
}