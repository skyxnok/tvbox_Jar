
let deviceId, token, userId;

//http://minotv.cn
let host = 'http://43.248.128.165:9000';


async function init(cfg) {
deviceId = getUUID();
let res = JSON.parse((await req(`${host}/v1/app/user/visitorInfo`,{
    headers: {
        'Content-Type': 'application/json',
        'User-Agent': 'okhttp/4.12.0',
        'deviceId': deviceId,
        'client': 'app',
        'deviceType': 'Android'
    }
})).content).data;
token = res.token;
userId = res.userId;
}


//生成UUID
function getUUID() {
    const pattern = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx';
    return pattern.replace(/[xy]/g, function(c) {
        const r = Math.random() * 16 | 0;
        return (c === 'x') ? r.toString(16) : 
               (r & 0x3 | 0x8).toString(16);
    });
}

//请求头
async function request(reqUrl, body, opt = {}) {
    const o = {
        headers: {
            "Cache-Control": "no-cache",
            "token": token ? token : '',
            "deviceId": deviceId,
            "client": "app",
            "deviceType": "Android",
            "Content-Type": "application/json;charset=UTF-8",
            "User-Agent": "okhttp/4.12.0"
        }
    };
    if (opt.encrypt) {
        // 加密请求：body 为明文（字符串或对象），signStr 为签名用拼接串（默认取明文）
        const plain = typeof body === 'string' ? body : JSON.stringify(body);
        const signStr = opt.signStr != null ? opt.signStr : plain;
        o.headers = hh2(signStr);
        o.method = 'POST';
        o.body = JSON.stringify({ "key": rsaEn(plain) });
        const resp = JSON.parse((await req(reqUrl, o)).content);
        return JSON.parse(rsaDe(resp.data));
    }
    if (body) {
        o.method = 'POST';
        o.body = typeof body === 'string' ? body : JSON.stringify(body);
    }
    return JSON.parse((await req(reqUrl, o)).content);
}

//详情请求头
function hh2(data) {
let skey = 'MIIDPDCCAiQCAQEwDQYJKoZIhvcNAQELBQAwYzEOMAwGA1UEAwwFemhhbmcxEDAOBgNVBAsMB2xpbmd6aGkxDzANBgNVBAoMBmdvbmdzaTERMA8GA1UEBwwIbGFuZ2ZhbmcxDjAMBgNVBAgMBWhlYmVpMQswCQYDVQQGEwJjbjAgFw0yNjA1MjIwMjMyNDJaGA8yMDc2MDUwOTAyMzI0MlowYzEOMAwGA1UEAwwFemhhbmcxEDAOBgNVBAsMB2xpbmd6aGkxDzANBgNVBAoMBmdvbmdzaTERMA8GA1UEBwwIbGFuZ2ZhbmcxDjAMBgNVBAgMBWhlYmVpMQswCQYDVQQGEwJjbjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAK4jK+Rl7YFZZ8GZ/Auxc0fmll1XC1+MTqhegCrQRFm78lOmUq2iYhRFKrbL3thzmH672d5eZbLhVjWbZAkfga3aF6mO6qaZygTRYAMTYOqeZKRadqy0AxEvs0SLNlfCTQb3//u15egcJgxgH6F8vCCPd4ILhMiuj1nrJG3mJJoLiCTILR+V+uJv/1qJO457tTz5rlH0ntbsvO2zDRCDEkGtGp+eM37AALJB+M3LKL+r5mvThXTVs/zyBECA2PzP8Q5U5cgDyxL2B2ivWleI8YrUOtb7qwNKHaSi7SDu2WbVfVRXYZ5I+3HSncNNGBuz62geKq63qSaH3eDQtFYjavsCAwEAATANBgkqhkiG9w0BAQsFAAOCAQEAhixGSQ4lQPgdz4wEPxoKpSYr+njaVSN1lxQwmLzh40E1cvJtvC/TN4JjlFvgXgMDf+Zpftr+zXW20HHnRFaGSFZKByFRMcZfpkInSAfiuL7kKg7vt9jcuTsRkCoDDje93DaVlIUhHJcNuzvdhzZclIsA38Iej8Cb8D6u+fflt09fu9u98A1nsweIYzE+k3uorMsWvIld2KeVCeYKMhMfQUwW3AnXVJ7dK5F7sn6TG9cOERU945Gy3NADcrIlAeWIXf3x2sB/d5nWsfY6sVVJIaRYOEVTrSybMeSYXkvWAenfFPN3YBXhtXW+vzuYlsKmx9KoivHm5KYUvuQamJkt0Q=='
let t = Math.floor(Date.now() / 1000);

let sha1hex = sha1X(skey, true, false);
let signb64 = btoa(`SaltLSFBTimestamp${t}Params${data}ClientappDeviceId${deviceId}`);

return {
    "snjm": rsaEn(`116`),
    "appsign": rsaEn(sha1hex),
    "timestamp": t,
    "sign": md5X(signb64).toUpperCase(),
    "Cache-Control": "no-cache",
    "token": token,
    "deviceId": deviceId,
    "client": "app",
    "deviceType": "Android",
    "Content-Type": "application/json;charset=UTF-8",
    "User-Agent": "okhttp/4.12.0"
}
}

//rsa加密
function rsaEn(data) {
let publicKey = `-----BEGIN PUBLIC KEY-----
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCoYt0BP77U+DM08BiI/QbSRIfxijXo85BTPqIM1Ow8BNwhLETzRIZ+dEwdWDbydG/PspgBAfRpGaYVdJYtvaC2JnoO8+Ik6qMWojfEJxSFLa0Pb0A892tun4gsxoEMjcreZ+YGyaBxAfqX0BSMfdrOgIYaZQjYrw9TRLlUT31QoQIDAQAB
-----END PUBLIC KEY-----`;
return rsaX('RSA/PKCS1', true, true, data, false, publicKey, true); 
}

//rsa解密
function rsaDe(data){
const key = `-----BEGIN PRIVATE KEY-----
MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCquQQ5r6+yJI8CDFkXRp8vUsdD45ov8EP12ooLs56ca2DQXaSNGS9910bAPVA9chkp0mKIvKqjAsHz5Tl9EeNPblarGEeJUIxpxZtiSqNTpvtiD/TjhpzuHYic7RAfQ/h7p/ypE8ymU42pYjsB5t26Mv6XgkLV+jzrSf73HlCuS0iMyLmt6zz3Mw9izM13EpB8iFLtfbbYymycKTx4RAmPQLwhNGex/AlUIYxXP4R2yyaa4W6mEtc6aME2QuzJFxPgP3HJ9NBx/LWVn4skxWjZ7zg+VRQRHnjyVaSLu3Z5gN5ITWCyE32qaHJa6WBahZj5jWhRyAG1bQ+xKJa8lBL5AgMBAAECggEAUwv9SjJ0PSwbhNuM2w23kcWquROWhYtTA91zGY4esehqB/IFgb2mpIh8Gje5OKqwIu/8jpd4SiOlRYdUF8sD0DfUYRZGdj2AkFNX6tBz8tVfo6wvbB6naA1lzzBij1L5JO3qsjS3cJFkb+kg2yP66AC2Z+0tpfk8eRhdtshAZwfcd1DEGt1uAvYL1eaUK9HRvpt9lPeGcHERDl2hBd4uyaF0K1O+zF9y59nYbTySWPxRZq3sFEE85xRMlstD7YZi7W2gKvMFRD4/FKmrZ3m7aKJRITtyKOyyPcYmepNv3Qv7kk59Pg38n2WWQ0Ra/bCH3E48YNCnQvZMpitkTfJhoQKBgQDbnROOYTP8OTJ6f/qhoGjxeO3x1VOaOp8l0x7b0SCfoqNGS0Cyiqj72BmJtPMPqSTjn6MmNzqbg1KOdhXyzNozs+i5ccW1M56j96mr5I/Z0FpE3oyIHNfDDBlf9M8YQqEF9oYxniYYft9oapO7cRQkHER6qpvnHTavwlv4m78CXwKBgQDHAjs2YlpKDdI1lcbZJCc7TwtH+Pd2bUki8YXafWNcPhITQHbOZjr310eK1QJC6GJncjkOqbX7yv3ivvTO35FZTQhuA1xEG1P00FG8bE0tHYPIwQHi9y0eA5cieMdo8E6XYria1mw/3fqSQEsfZyJlR32JQIoGAipM8iO1X2nZpwKBgDkMFIhnt5lNQk+P7wsNIDWZtDWdtJnboHuy29E+Abt2A/O+mI/IdRz2hau/1WO8DFkUnszOi+rZshhPlGP90rCbi1igtTrcrdjp/KkqNjPea5R4OwkgdOu1uOG0NheXNzzVTQaWjk7Opjn5dWa7eP/oV+GFb/oZHJuLYVizHGsBAoGADA7rjZEKDYCm4w5PPSr+oY5ZjaPdQrS+gLqHtMRyN82fBMGcMUdqfUfzEstzVqCEDeaS5HuOBlK3bXzKkppjUTjksN3NQmcxgBz7RuJ9DqXCLXDcb2cwuafYCYOt+YLOEEgwDVm+t2P44dG5e46hO+fICH/7nP+WlpD5buz4GfMCgYB57r3g/6hi9WUDnfc7ZAzWMqR0EhJVYKYy+KFEtdIPzhkkIHq5RASe88E9kzoGoZFdb3tIjvGZWcHerirrqWkMsuQtP/Qi0zjieid5tAPj+r4kbiCVTw0E0jnmPBzGInQi7lpeTTKnG1fbyS5lBS+WmHfIuzpECgCkxhaT+LJJkg==
-----END PRIVATE KEY-----`;
return rsaXS('RSA/PKCS1', false, false, data, true, key, false); //rsaXS自动分块解密
}

//分类
async function home (filter) {
let html = await request(`${host}/v1/app/screen/screenType`, 'POST')

//一级
let classes = html.data.map(tp => ({
  type_id: tp.id,
  type_name: tp.name
}))

//二级筛选
let filterObj = {};
for (let type of html.data) {
  let filters = [];
  const keyMap = { "地区": "area", "类型": "class", "年份": "year" };
  for (const group of type.children) {
    if (group.children && group.children.length) {
      filters.push({
        key: keyMap[group.name] || group.name,
        name: group.name,
        value: group.children.map(c => ({ n: c.name, v: c.id }))
      });
    }
  }

  filters.push({
    key: 'sort',
    name: '排序',
    value: [{v: "NEWEST", n: "最新"}, {v: "COLLECT", n: "评分"}, {v: "HOT", n: "热搜"}]
  });

  filterObj[type.id] = filters;
}

return JSON.stringify({ class: classes, filters: filterObj });
}


//主页推荐
async function homeVod() {
let data = {
    "condition": "64",
    "pageNum": "1",
    "pageSize": "6"
};
let tj = await request(`${host}/v1/app/recommend/recommendSubList`, data);
//推荐
let videos = tj.data.records.map(item => ({
    vod_id: `${item.id}@@${item.typeId}`,
    vod_name: item.name,
    vod_pic: item.cover,
    vod_remarks: item.remarks
}));

return JSON.stringify({list: videos});
}


//分类
async function category (tid, pg, filter, extend) {
let body = {
	"condition": {
		"classify": extend.class || '',
		"region": extend.area || '',
		"sreecnTypeEnum": extend.sort || 'POPULARITY',
		"typeId": tid,
		"year": extend.year || ''
	},
	"pageNum": pg,
	"pageSize": 40
};

let html = await request(`${host}/v1/app/screen/screenMovie`,body);
let videos = html.data.records.map(item => ({
    vod_id: `${item.id}@@${item.typeId}`,
    vod_name: item.name,
    vod_pic: item.cover,
    vod_remarks: item.remarks,
    vod_year: item.year
}));

return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let [ ids, typeId ] = id.split('@@');
let res = (await request(`${host}/v1/app/play/movieDesc`, JSON.stringify({"id": Number(ids),"typeId": typeId}))).data; //简介请求

let arr = [
  "episodeId", "",
  "episodeIndex", "",
  "id", ids,
  "playerId", "",
  "source", "0",
  "typeId", typeId,
  "userId", userId
].join("");

let data = JSON.stringify({"episodeId":"","episodeIndex":"","id":Number(ids),"playerId":"","source":0,"typeId":typeId,"userId":userId});

let html = await request(`${host}/v1/app/play/movieDetails`, data, { encrypt: true, signStr: arr });
let play_from = html.moviePlayerList.map(item => `${item.moviePlayerName.replace(/【.*?】/,'')} [${item.code}]`).join('$$$');

let play_url = [];
let playUrl = html.episodeList.map(item => `${item.episode.replace(/ 4k/gi,'')}$${item.id}@@${id}@@${html.playerId}`).join('#')
play_url.push(playUrl);
let pids = html.moviePlayerList.map(item => item.id) //其他线路列表id

// 只并发 pids 线路：request 已恢复为同步 req，并发必须直接用 http 异步
if (pids.length > 1) {
    const tasks = pids.slice(1).map(pid => {
        let arr = [
            "episodeId", "",
            "episodeIndex", "",
            "id", ids,
            "playerId", pid,
            "source", "0",
            "typeId", typeId,
            "userId", userId
        ].join("");
        let data = JSON.stringify({"episodeId":"","episodeIndex":"","id":Number(ids),"playerId":pid,"source":0,"typeId":typeId,"userId":userId});
        const o = {
            headers: hh2(arr),
            method: 'POST',
            body: JSON.stringify({ "key": rsaEn(data) })
        };
        return http(`${host}/v1/app/play/movieDetails`, o).then(r => {
            const resp = JSON.parse(r.content);
            return JSON.parse(rsaDe(resp.data));
        });
    });
    const others = await Promise.all(tasks);
    for (const lineRes of others) {
        let playid = lineRes.playerId
        let linePlayUrl = lineRes.episodeList.map(item => `${item.episode.replace(/ 4k/gi,'')}$${item.id}@@${id}@@${playid}`).join('#')
        play_url.push(linePlayUrl);
    }
}

var vod = {
    "type_name": res.classify,
    "vod_year": res.year,
    "vod_area": res.area,
//    "vod_remarks": '',
    "vod_actor": res.star,
    "vod_director": res.director,
    "vod_content": res.introduce,
    "vod_play_from": play_from, 
    "vod_play_url": play_url.join('$$$')
    }

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
let [ episodeId, ids, typeId, playerId ] = id.split('@@');
let arr = [
  "episodeId", episodeId,
  "episodeIndex", "",
  "id", ids,
  "playerId", playerId,
  "source", "0",
  "typeId", typeId,
  "userId", userId
].join("");

let data = JSON.stringify({"episodeId":episodeId,"episodeIndex":"","id":Number(ids),"playerId":playerId,"source":0,"typeId":typeId,"userId":userId});
let res = await request(`${host}/v1/app/play/movieDetails`, data, { encrypt: true, signStr: arr });
let url = (await request(`${host}/v1/app/play/analysisMovieUrl?playerUrl=${res.url}&playerId=${res.playerId}`)).data;

return JSON.stringify({parse: 0, url: url});
}

//搜索
async function search (wd, quick, pg=1) {
let data = JSON.stringify({"condition": {"value": wd},"pageNum": pg,"pageSize": 40});
let html = await request(`${host}/v1/app/search/searchMovie`, data);
let videos = html.data.records.map(item => ({
    vod_id: `${item.id}@@${item.typeId}`,
    vod_name: item.name,
    vod_pic: item.cover,
    vod_remarks: item.remarks,
    vod_year: item.year
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

