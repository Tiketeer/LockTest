import {uuidv4} from "https://jslib.k6.io/k6-utils/1.4.0/index.js";
import http from "k6/http";

import {check} from "k6";

const setupVar = {
    userNum: 300, // 구매자 수 (vUser),
    ticketStock: 50, // 티켓 재고
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
    const test = __ENV || 0;
    console.log(test);

    const userMetadata = Array(setupVar.userNum)
        .fill()
        .map(() => {
            return {
                email: `test-buyer-${uuidv4()}@test.com`,
                password: commonPwd,
                role: "BUYER",
                token: "",
            };
        });

    const sellerEmail = `test-seller-${uuidv4()}@test.com`;

    wrapWithTimeLogging("유저 생성", () => {
        createUsers({
            members: [
                ...userMetadata,
                {email: sellerEmail, password: commonPwd, role: "SELLER"},
            ],
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
        return createTicketing(
            {
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
            },
            sellerEmail,
            commonPwd
        );
    });

    wrapWithTimeLogging(
        `유저 일괄 로그인 (유저 수: ${userMetadata.length})`,
        () => {
            const accessTokens = batchLogin(userMetadata);
            for (let idx = 0; idx < userMetadata.length; idx++) {
                userMetadata[idx].token = accessTokens[idx];
            }
        }
    );

    return {userMetadata, ticketingId};
}

export default function ({userMetadata, ticketingId}) {
    const userCounter = __VU;

    const accessToken = userMetadata[userCounter].token;

    puchase(ticketingId, 1, accessToken);
}

function createUsers(users) {
    const response = http.post(
        domain + "/api/stress-test/members",
        JSON.stringify(users),
        {
            headers: commonHeader,
        }
    );

    check(response, {
        "status check after create user": (r) => r.status === 200,
    });
}

function createTicketing(ticketingMetadata, sellerEmail, sellerPwd) {
    const accessToken = login(sellerEmail, sellerPwd);
    const response = http.post(
        domain + "/api/ticketings",
        JSON.stringify(ticketingMetadata),
        {
            headers: commonHeader,
            cookies: {accessToken},
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

function login(email, password) {
    const response = http.post(
        domain + "/api/auth/login",
        JSON.stringify({email, password}),
        {
            headers: commonHeader,
        }
    );

    check(response, {
        "status check after login": (r) => r.status === 200,
    });

    return response.cookies["accessToken"][0].value;
}

function batchLogin(users) {
    const batchReqs = users.map(({email, password}) => ({
        method: "POST",
        url: domain + "/api/auth/login",
        body: JSON.stringify({email, password}),
        params: {
            headers: commonHeader,
        },
    }));

    const responses = http.batch(batchReqs);

    responses.map((response) =>
        check(response, {
            "status check after batch login": (r) => r.status === 200,
        })
    );

    return responses.map((res) => res.cookies["accessToken"][0].value);
}

function puchase(ticketingId, count, accessToken) {
    const response = http.post(
        domain + "/api/purchases",
        JSON.stringify({
            ticketingId,
            count,
        }),
        {
            headers: commonHeader,
            cookies: {accessToken},
        }
    );

    check(response, {
        "status check after create purchase": (r) => r.status === 201,
    });
}

function wrapWithTimeLogging(tag, callback) {
    const start = new Date();
    const result = callback();
    const end = new Date();
    console.log(`${tag}: 소요시간 - ${end.getTime() - start.getTime()}ms`);
    return result;
}
