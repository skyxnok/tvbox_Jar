import "assets://js/lib/crypto-js.js";

let host, TOKEN, TOKEN_ID;

let KEYS = rsaEncrypt(JSON.stringify({ "iv": "2U3IrJL8szAKp0Fj", "key": "mvXBSW7ekreItNsT" }));
let deviceId = String(864150060000000 + Math.floor(Math.random() * 10000));
let deviceKey = Array.from({ length: 40 }, () => "0123456789ABCDEF"[Math.floor(Math.random() * 16)]).join('');

async function init(cfg) {
    host = cfg.ext?.host || "https://api.36kzbh85.com";
    try {
        await signUp();
        await refreshToken();
    } catch (e) {
        // 认证失败兜底
    }
    if (!TOKEN) TOKEN = "bd9be2be616d26492bb71879795511cc.0e4d6c97da0ecb2281f77ca977212f4afb233a30cdd7d18652bc632e658710347be33b01ef5ee47df719ef21a0a50b9bd7a645504a9cd50167be16d5d0e763a159d6e900a2923a414d3f5616d987aa10edf68817d0a18a2beff894840d2dbb0c22ce6a3a4f1de4cebb52171fee07d1f9cf1d5590385f5f7ef6e01d1850974aa220eb5178c89e61c24411af9b9a19435e.82f5703aade6eb6dcb5f20d29b1fd75e3213ea2df683a8a5581740312b564579";
}

// ---------- 设备注册与认证----------
async function signUp() {
    const res = await request(`${host}/App/Authentication/Device/signUp`, {
        new_key: deviceKey, old_key: "aLFBMWpxBrIDAD1Si/KVvm41", phone_type: 1, code: ""
    });
    applyAuth(res);
}

async function refreshToken() {
    const res = await request(`${host}/App/Authentication/Authenticator/refresh`, {});
    applyAuth(res);
}

function applyAuth(res) {
    if (res && res.token) {
        TOKEN = res.token;
        if (res.app_user_id) TOKEN_ID = res.app_user_id;
    }
}

//加密
function en(data) {
    const key = CryptoJS.enc.Utf8.parse("mvXBSW7ekreItNsT");
    const iv = CryptoJS.enc.Utf8.parse("2U3IrJL8szAKp0Fj");
    const encrypted = CryptoJS.AES.encrypt(data, key, {
        iv: iv,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
    });
    return encrypted.ciphertext.toString(CryptoJS.enc.Hex).toUpperCase()
}

//rsa加密
function rsaEncrypt(data) {
let publicKey = `-----BEGIN PUBLIC KEY-----
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDUM5+/y8sPsWkd1/RQS64X259EUwxFXFE5HlA65MqrxnPs0JqoSRojSDy5QhwvROlaD6TwRQHKMY2OAZ6SnQeUJsChTEFIR9qUkwrs3/MVUMxjsv6JS6Oe/juclyJGTgVmDhB55EafXsD0SQYVj/QXXsxR6ewR5E2kL52yAAD4yQIDAQAB
-----END PUBLIC KEY-----`;
return rsaX('RSA/PKCS1', true, true, data, false, publicKey, true);
}

//rsa解密
function rsaDecrypt(data) {
let privateKey =  `-----BEGIN PRIVATE KEY-----
MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGAe6hKrWLi1zQmjTT1ozbE4QdFeJGNxubxld6GrFGximxfMsMB6BpJhpcTouAqywAFppiKetUBBbXwYsYU1wNr648XVmPmCMCy4rY8vdliFnbMUj086DU6Z+/oXBdWU3/b1G0DN3E9wULRSwcKZT3wj/cCI1vsCm3gj2R5SqkA9Y0CAwEAAQKBgAJH+4CxV0/zBVcLiBCHvSANm0l7HetybTh/j2p0Y1sTXro4ALwAaCTUeqdBjWiLSo9lNwDHFyq8zX90+gNxa7c5EqcWV9FmlVXr8VhfBzcZo1nXeNdXFT7tQ2yah/odtdcx+vRMSGJd1t/5k5bDd9wAvYdIDblMAg+wiKKZ5KcdAkEA1cCakEN4NexkF5tHPRrR6XOY/XHfkqXxEhMqmNbB9U34saTJnLWIHC8IXys6Qmzz30TtzCjuOqKRRy+FMM4TdwJBAJQZFPjsGC+RqcG5UvVMiMPhnwe/bXEehShK86yJK/g/UiKrO87h3aEu5gcJqBygTq3BBBoH2md3pr/W+hUMWBsCQQChfhTIrdDinKi6lRxrdBnn0Ohjg2cwuqK5zzU9p/N+S9x7Ck8wUI53DKm8jUJE8WAG7WLj/oCOWEh+ic6NIwTdAkEAj0X8nhx6AXsgCYRql1klbqtVmL8+95KZK7PnLWG/IfjQUy3pPGoSaZ7fdquG8bq8oyf5+dzjE/oTXcByS+6XRQJAP/5ciy1bL3NhUhsaOVy55MHXnPjdcTX0FaLi+ybXZIfIQ2P4rb19mVq1feMbCXhz+L1rG8oat5lYKfpe8k83ZA==
-----END PRIVATE KEY-----`; //私钥
return rsaX('RSA/PKCS1', false, false, data, true, privateKey, false);
}

//请求处理函数
async function request(urls, params) {
    const t = Math.round(new Date() / 1000);
    const request_key = en(JSON.stringify(params));
    const hh = {
        "api-ver": "3.0.3.2", "Ver": "3.0.3.2", "lang": "zh_cn",
        "Cache-Control": "no-cache", "Version": "2509018",
        "PackageName": "com.nfc2e6fc21.d6a64f4fd1.p004a191e220251009",
        "code": "GZ0369", "deviceId": deviceId,
        "Referer": host, "User-Agent": "okhttp/3.12.0",
        "Content-Type": "application/x-www-form-urlencoded"
    };

    const sign = CryptoJS.MD5(`token_id=,token=${TOKEN},phone_type=1,request_key=${request_key},app_id=1,time=${t},keys=${KEYS}*&zvdvdvddbfikkkumtmdwqppp?|4Y!s!2br`).toString().toUpperCase();
    const body = `token=${TOKEN}&token_id=&phone_type=1&time=${t}&phone_model=xiaomi-22021211rc&keys=${encodeURIComponent(KEYS)}&request_key=${request_key}&signature=${sign}&app_id=1&ad_version=1`;

    const html = JSON.parse((await req(urls, { body, headers: hh, method: 'POST' })).content);
    // 解密响应（rsa 解出 key/iv，再用其 AES 解密 response_key）
    const keyInfo = JSON.parse(rsaDecrypt(html.data.keys));
    const decrypted = CryptoJS.AES.decrypt(
        { ciphertext: CryptoJS.enc.Hex.parse(html.data.response_key) },
        CryptoJS.enc.Utf8.parse(keyInfo.key),
        { iv: CryptoJS.enc.Utf8.parse(keyInfo.iv), mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7 }
    ).toString(CryptoJS.enc.Utf8);
    return JSON.parse(decrypted);
}

//分类
async function home (filter) {
let classes = [{"type_id":1,"type_name":"电影"},{"type_id":2,"type_name":"电视剧"},{"type_id":4,"type_name":"动漫"},{"type_id":3,"type_name":"综艺"},{"type_id":64,"type_name":"短剧"}]
let filterObj = {"1":[{"key":"area","name":"地区","value":[{"n":"地区","v":"0"},{"n":"大陆","v":"大陆"},{"n":"香港","v":"香港"},{"n":"台湾","v":"台湾"},{"n":"欧美","v":"俄罗斯,加拿大,德国,意大利,法国,欧美,美国,英国,西班牙"},{"n":"日本","v":"日本"},{"n":"韩国","v":"韩国"},{"n":"泰国","v":"泰国"},{"n":"其他","v":"其他,印度,新加坡,马来西亚"}]},{"key":"sub","name":"类型","value":[{"n":"动作片","v":5},{"n":"悬疑片","v":29},{"n":"喜剧片","v":6},{"n":"爱情片","v":7},{"n":"科幻片","v":8},{"n":"恐怖片","v":9},{"n":"剧情片","v":10},{"n":"战争片","v":11},{"n":"动画片","v":36},{"n":"纪录片","v":20},{"n":"灾难片","v":38},{"n":"犯罪片","v":61}]},{"key":"year","name":"年份","value":[{"n":"年份","v":"0"},{"n":"2026","v":"2026"},{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2021","v":"2021"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"},{"n":"2018","v":"2018"},{"n":"2017","v":"2017"},{"n":"2016","v":"2016"},{"n":"10-15年","v":"2015,2014,2013,2012,2011,2010"},{"n":"00年代","v":"2000,2001,2002,2003,2004,2005,2006,2007,2008,2009"},{"n":"90年代","v":"1990,1991,1992,1993,1994,1995,1996,1997,1998,1999"},{"n":"80年代","v":"1980,1981,1982,1983,1984,1985,1986,1987,1988,1989"},{"n":"更早","v":"2"}]},{"key":"sort","name":"排序","value":[{"n":"综合","v":"d_id"},{"n":"最新","v":"d_addtime"},{"n":"高分","v":"d_score"}]}],"2":[{"key":"area","name":"地区","value":[{"n":"地区","v":"0"},{"n":"大陆","v":"大陆"},{"n":"香港","v":"香港"},{"n":"台湾","v":"台湾"},{"n":"欧美","v":"俄罗斯,加拿大,德国,意大利,法国,欧美,美国,英国,西班牙"},{"n":"日本","v":"日本"},{"n":"韩国","v":"韩国"},{"n":"泰国","v":"泰国"},{"n":"其他","v":"其他,印度,新加坡,马来西亚"}]},{"key":"sub","name":"类型","value":[{"n":"国产剧","v":12},{"n":"香港剧","v":13},{"n":"台湾剧","v":14},{"n":"欧美剧","v":15},{"n":"日本剧","v":16},{"n":"韩国剧","v":17},{"n":"海外剧","v":18},{"n":"泰国剧","v":19},{"n":"新加坡","v":69}]},{"key":"year","name":"年份","value":[{"n":"年份","v":"0"},{"n":"2026","v":"2026"},{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2021","v":"2021"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"},{"n":"2018","v":"2018"},{"n":"2017","v":"2017"},{"n":"2016","v":"2016"},{"n":"10-15年","v":"2015,2014,2013,2012,2011,2010"},{"n":"00年代","v":"2000,2001,2002,2003,2004,2005,2006,2007,2008,2009"},{"n":"90年代","v":"1990,1991,1992,1993,1994,1995,1996,1997,1998,1999"},{"n":"80年代","v":"1980,1981,1982,1983,1984,1985,1986,1987,1988,1989"},{"n":"更早","v":"2"}]},{"key":"sort","name":"排序","value":[{"n":"综合","v":"d_id"},{"n":"最新","v":"d_addtime"},{"n":"高分","v":"d_score"}]}],"4":[{"key":"area","name":"地区","value":[{"n":"地区","v":"0"},{"n":"大陆","v":"大陆"},{"n":"香港","v":"香港"},{"n":"台湾","v":"台湾"},{"n":"欧美","v":"俄罗斯,加拿大,德国,意大利,法国,欧美,美国,英国,西班牙"},{"n":"日本","v":"日本"},{"n":"韩国","v":"韩国"},{"n":"泰国","v":"泰国"},{"n":"其他","v":"其他,印度,新加坡,马来西亚"}]},{"key":"sub","name":"类型","value":[{"n":"中国动漫","v":30},{"n":"日本动漫","v":31},{"n":" 欧美动漫","v":33}]},{"key":"year","name":"年份","value":[{"n":"年份","v":"0"},{"n":"2026","v":"2026"},{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2021","v":"2021"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"},{"n":"2018","v":"2018"},{"n":"2017","v":"2017"},{"n":"2016","v":"2016"},{"n":"10-15年","v":"2015,2014,2013,2012,2011,2010"},{"n":"00年代","v":"2000,2001,2002,2003,2004,2005,2006,2007,2008,2009"},{"n":"90年代","v":"1990,1991,1992,1993,1994,1995,1996,1997,1998,1999"},{"n":"80年代","v":"1980,1981,1982,1983,1984,1985,1986,1987,1988,1989"},{"n":"更早","v":"2"}]},{"key":"sort","name":"排序","value":[{"n":"综合","v":"d_id"},{"n":"最新","v":"d_addtime"},{"n":"高分","v":"d_score"}]}],"3":[{"key":"area","name":"地区","value":[{"n":"地区","v":"0"},{"n":"大陆","v":"大陆"},{"n":"香港","v":"香港"},{"n":"台湾","v":"台湾"},{"n":"欧美","v":"俄罗斯,加拿大,德国,意大利,法国,欧美,美国,英国,西班牙"},{"n":"日本","v":"日本"},{"n":"韩国","v":"韩国"},{"n":"泰国","v":"泰国"},{"n":"其他","v":"其他,印度,新加坡,马来西亚"}]},{"key":"sub","name":"类型","value":[{"n":"大陆综艺","v":22},{"n":"港台综艺","v":23},{"n":"日韩综艺","v":24},{"n":"欧美综艺","v":25}]},{"key":"year","name":"年份","value":[{"n":"年份","v":"0"},{"n":"2026","v":"2026"},{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2021","v":"2021"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"},{"n":"2018","v":"2018"},{"n":"2017","v":"2017"},{"n":"2016","v":"2016"},{"n":"10-15年","v":"2015,2014,2013,2012,2011,2010"},{"n":"00年代","v":"2000,2001,2002,2003,2004,2005,2006,2007,2008,2009"},{"n":"90年代","v":"1990,1991,1992,1993,1994,1995,1996,1997,1998,1999"},{"n":"80年代","v":"1980,1981,1982,1983,1984,1985,1986,1987,1988,1989"},{"n":"更早","v":"2"}]},{"key":"sort","name":"排序","value":[{"n":"综合","v":"d_id"},{"n":"最新","v":"d_addtime"},{"n":"高分","v":"d_score"}]}],"64":[{"key":"sort","name":"排序","value":[{"n":"综合","v":"d_id"},{"n":"最新","v":"d_addtime"},{"n":"高分","v":"d_score"}]}]};

return JSON.stringify({ class: classes, filters: filterObj });
}

//主页推荐
async function homeVod() {
let t = Math.round(new Date() / 1000);
let html = await request(`${host}/App/IndexList/choiceList`, { ns: "", nt: t, pid: "1" })

let videos = html.list.map(item => ({
    vod_id: item.vod_id,
    vod_name: item.c_name,
    vod_pic: item.c_pic,
    vod_remarks: item.cf_name,
    vod_year: item.new_continue || '',
    style: {"type": "rect", "ratio": 1.485 }
}));

return JSON.stringify({ list: videos })
}

//分类
async function category (tid, pg, filter, extend) {
let html = await request(`${host}/App/IndexList/indexList`, {
    tid: tid, page: pg, sort: extend.sort || 'd_id',
    area: extend.area || '', sub: extend.sub || '', year: extend.year || '', pageSize: "30"
})

let videos = html.list.map(item => ({
    vod_id: item.vod_id,
    vod_name: item.vod_name,
    vod_pic: item.vod_pic,
    vod_remarks: item.new_continue,
    vod_year: item.vod_year
}));

return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let t = Math.round(new Date() / 1000);
let html = await request(`${host}/App/Resource/Vurl/show`, { vurl_cloud_id: "2", vod_d_id: id })
let info = (await request(`${host}/App/IndexPlay/playInfo`, { mobile_time: t, vod_id: id })).vodInfo

let play_url = html.list.map(item => {
  let lastParamValue = null;
  for (const key in item.play) {
    if (item.play[key].param) {
      lastParamValue = item.play[key].param;
    }
  }
  return `${item.title}$${lastParamValue}`; 
}).join('#');

var vod = {
    "vod_name": info.vod_name,
    "type_name": info.videoTag.join(' / '),
    "vod_year": info.vod_year.split('-')[0] || info.vod_year,
    "vod_area": info.vod_area || '',
    "vod_remarks": info.new_continue,
    "vod_actor": info.vod_actor,
    "vod_director": info.vod_director,
    "vod_content": info.vod_use_content,
    "vod_play_from": '瓜子', 
    "vod_play_url": play_url
    }

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
let params = {};
id?.split('&').forEach(item => {
  const [k, v] = item.split('=');
  params[decodeURIComponent(k)] = decodeURIComponent(v ?? '');
});

let html = await request(`${host}/App/Resource/VurlDetail/showOne`, params)

return JSON.stringify({ parse: 0, url: html.url, header:{"User-Agent": "Lavf/57.83.100", "Referer": "http://WJiZxLXA2.com/"} })
}

//搜索
async function search (wd, quick, pg=1) {
let t = Math.round(new Date() / 1000);
let html = await request(`${host}/App/Index/findMoreVod`, { keywords: wd, ns: "", nt: t, order_val: "1" })

let videos = html.list.map(item => ({
    vod_id: item.vod_id,
    vod_name: item.vod_name,
    vod_pic: item.vod_pic,
    vod_remarks: item.new_continue,
    vod_year: item.vod_year?.split("-")[0] || ''
}));

return JSON.stringify({ limit: videos.length, list: videos });
}

export function __jsEvalReturn() {
  return {
      init: init,
      home: home,
      homeVod: homeVod,
      category: category,
      detail: detail,
      play: play,
      search: search
  };
}