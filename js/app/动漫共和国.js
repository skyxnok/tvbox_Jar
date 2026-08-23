//import "assets://js/lib/crypto-js.js";

//http://oneghg.com  http://pc.oneghg.com
let host;

async function init(cfg) {
let res = JSON.parse((await req(`http://175.178.11.16:7862/app/config/host`, {
        headers: gethh()
    })).content).data

let key = 'ziISjqkXPsGUMRNGyWigxDGtJbfTdcGv';
let iv  = 'WonrnVkxeIxDcFbv';
let decrypted = aesX('AES/CBC/PKCS5', false, res, true, key, iv, false);

let bb = atob(decrypted)
let cc = JSON.parse(bb).host
host = cc ? cc : 'http://bljhm.xn--vhqr42drhf5k7b.com'
}

//响应体rsa解密+aes解密
function rsaDecrypt(data) {
let datas = data.split('.')
let privateKey =  `-----BEGIN PRIVATE KEY-----
MIIEowIBAAKCAQEAo0aDQMwcWpsCrvI0J222vaB6zRuAZ1U9VSjhCKwqicXodMlgfwhKhjoZ7jVoRKZ6hZ8GNDoANJ7FyV23Cet++aDot4JhkbL4RHBfi8sqUnjWYf+jGI/WWgIpGiJKTvzsG+by3Wr0f4c9Ajb334Kwu7bLFpU3nKqDDIfTYrF0hsDiWzJ4CN9pOnYa4FZ+pfPy0YaFxLT5MDbsBQ2lqWNxb+OVrjVX+8hiaYnWjwmHlIDAao2irnu+YIGduwv+/PNK6DXl0NN3W0S7PRT9wyBX8j1Tus230dAlHRj0hfCbrqfAX1vVTsHxgUnLgUQyQ4PITbJ7SRUqVsY8Y/Y9T4eVNQIDAQABAoIBAFj0HKQbz8LJOvAHQsTMcEfle6HtPsqNVQnlaJyp987xxNCDug/be4afdushq7njHVNZLS8c/mmsqsMnTIaaB6aGtOLtpKyVXc8jjdqCiH9AGERx2vCRxM1q6eu0DNn1z8jvzRc2oxgrnOBtBCSAdjr+vqyCBTdUamtUQKU/WuXip1LwjQ7C2L7AoILBmIFGAjN1rXQqsWi73No1/Xvov9y25DZnIgnVTGi++Ue9FXh3qfV7/CQkZ1X6dqQ01dW4PZtpjdI2Wr6A+IC8iRaj5HZYcJkJUmNHrFKxL0Z7b2yH/mmr9YXAccIk7yFMFSyhGSoqT1RYheOi5vvSVE0WY6ECgYEA0mQb6lBI87FYG79pc+Cb/G/3idZAT9DoTT2DaCFHLG1QKHEabLj9vad3v6AjRnFRRoUv3WSuLLIfha5vcpVZ13TvSlqVVd6n7XwSIfnthDQ2ypB75VJT//DWYJL4gexGZt0s/smQpK8iB1lKGfKqkOFU+J88wPopyN1QMuYBmVkCgYEAxqurUF+abXcYvTwdAbAGH0gkaXAbeLbrwDKZ/qcW+QGUvxoin5j6ZKUUhR1UMD+bF78jEQXM07+tpxcl1jM67aYWZTsPtqKi571Y2urIRmiBDDSb5B4AxrUSYAqq6RkA6lXDUxAaDT3Tz/TC1V2dtxwJssS/bEOwr/Z127nqAz0CgYAsZDsPoYkDAjRZBnY1oPrItMdCKha/wJCDW6tSWVMvKJF1NwggUJgZYDCAGkXXIynG+2syB4BIpfzItBmHz8N5Fo823Q4NZEGCdl9NE/Ltpia5bur1Y/2dTy+siNYuc7AXHCvWRqliViGT818TQoSCtUi8fLzQ6vfODgRR+P31YQKBgCDg/EMa71W6Zg+7SRmkZf77U3tXoFREAZQXS8EHKhgfmNxfmOdMy/OoFlNJXUt221X8vfLtQM6yZCzI+ewPImt+Fyq9sYYKOGedwHzKakasuN6qPjpsdLht8xKN8WcOSkZ91wuCGK2kU8+QtEXXbmiFbV12ji9+rFkSssKgbAgJAoGBAI1uglzCpiaLDUpMjqnk83xWu6rujFEWdyLHmzS9QNtQfbXNq4zCdPp8tofBtmz+7g30RwFYSJCHUp+kp0rwiNjj4JOMf9x4UtGMel8+CghY4uZP05GWjqRM51iy21+njFSSFxbs6PZTMcrNS5lQH0l0gxXmBa6m6bFFGRBMbR9s
-----END PRIVATE KEY-----`; //私钥
let key = rsaX('RSA/PKCS1', false, false, datas[0], true, privateKey, false);
let iv  = key.split('').reverse().join('');
let decrypted = aesX('AES/CBC/PKCS5', false, datas[1], true, key, iv, false);
return JSON.parse(decrypted);
}

//解密播放链
function de(data) {
let key = 'J5jQnzGVRfCe4CUk';  //MD5_AES_IV
let iv  = 'UY9kxQEtk8Dn08Kr';  //MD5_AES_KEY
let decrypted = aesX('AES/CBC/PKCS5', false, data, true, key, iv, false);
return decrypted
}

//加密playurl MD5
function en(data) {
const key = 'UY9kxQEtk8Dn08Kr';  //MD5_AES_KEY
const iv  = 'J5jQnzGVRfCe4CUk';  //MD5_AES_IV
//模式  加密  内容  内容是不是b64  key  iv   输出b64
return aesX('AES/CBC/PKCS5', true, data, false, key, iv, true)
}

//播放请求体
function getbody() {
let key = getrand(16)
let iv = key.split('').reverse().join('')
let publicKey = `-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq+BSQiNSojdRQf5Ie9VC+jmlCkHbE93ei0Tl2AtaBSOxovTR3o8KCQtJF4FBwpC3k6UYJAdIq6nXA+zfJv0ptF9Ow6TQUjGytLUX5S0NNyOADGV07eIuBtA6j+l6vZ+T1iikeEkSjZkrhmpm1yh/PTA8VaDSN1EOS3NWZWk56LKofvET12n88mJgBpWwyqD6iImzwLdwWHbWtk7xSI2+zENffzP6LJk5PApYQtIXaR1nCJ/TCXgbqWRFjXpT9kiaID4cvqMT7WnBxX1zSlj0e0PYMOEWxt6fioo/ksnWoyAK8hpbgHgDuPe6mqEvLPR8tAPMhDP46+yEOLNwexzr9wIDAQAB
-----END PUBLIC KEY-----`;

let res = `{"checkAD":{"SplashAD":{"show":0,"load":0},"InteractionAD":{"show":0,"load":0},"BannerAD":{"show":0,"load":0},"FullScreenVideoAD":{"show":0,"load":0},"RewardVideoAD":{"show":0,"load":0}}}`

let body1 = rsaX('RSA/PKCS1', true, true, res, false, publicKey, true);
let body2 = aesX('AES/CBC/PKCS5', true, res, false, key, iv, true)
return `${body1}.${body2}`
}


//随机字符
function getrand(length) {
  // 如果是4位数字，生成4位数字随机码（补0）
  if (length === 4) {
    const randomNum = Math.floor(Math.random() * 10000);
    return randomNum.toString().padStart(4, '0');
  }
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let key = '';
    for (let i = 0; i < length; i++) {
      const randomIndex = Math.floor(Math.random() * chars.length);
      key += chars[randomIndex];
    }
    return key;
}

//获取时间
function getTimestampStr() {
  const now = new Date();
  // 当前时间 + 8小时时区 + 10分钟缓存
   const target = new Date(now.getTime() + 8 * 60 * 60 * 1000 + 10 * 60 * 1000);

  const year = target.getUTCFullYear();
  const month = String(target.getUTCMonth() + 1).padStart(2, '0');
  const day = String(target.getUTCDate()).padStart(2, '0');
  const hours = String(target.getUTCHours()).padStart(2, '0');
  const minutes = String(target.getUTCMinutes()).padStart(2, '0');

  return year + month + day + hours + minutes;
}

//m3u8播放链接生成签名
function getM3u8(url, data) {
  if (!url.endsWith('.m3u8')) return url;

  const match = url.match(/(https?:\/\/[^/]+)(\/.+)/);
  if (!match) return url;
  const [_, domain, path] = match;

  const timestampStr = getTimestampStr();
  const md5key = data.match(/M3U8_AUTH\s*=\s*\{[\s\S]*?KEY\s*=\s*"([^"]+)"/)[1]
  const signStr = md5key + timestampStr + path;
  const md5hash = md5X(signStr).toLowerCase();

  return replaceDomain(`${domain}/${timestampStr}/${md5hash}${path}`, data)
}

//播放链域名替换
function replaceDomain(url, data) {
    // 正则匹配 {"源域名", "目标域名"}
    const ruleReg = /\{\s*"([^"]+)"\s*,\s*"([^"]+)"\s*\}/g;
    if (data) {
        let match;
        while ((match = ruleReg.exec(data)) !== null) {
            let aa = match[1].replace(/%\./g, '.');
            let bb = match[2];
            url = url.replace(aa, bb);
        }
    }

    if (url.includes('anixx.r2')) {
        return 'https://sns-music.xhscdn.com/104002e031m0qe7o84s0m6saf3o';
    }
    return url;
}

//请求头
function gethh() {
  let t = Date.now()
  let key = 'ziISjqkXPsGUMRNGyWigxDGtJbfTdcGv';
  let iv = 'WonrnVkxeIxDcFbv';
  // base64编码后在AES加密
  let data = btoa(`3.0.0.3-${t}-Android-1.0.0.7-6bd5d038ab0f4ab8b7de630bbff75e7b`)
  let au = aesX('AES/CBC/PKCS5', true, data, false, key, iv, true)
  return {
    "user-agent": "Dart/3.6 (dart:io)",
    "x-version": "2024-09-24",
    "appid": "4150439554430627",
    "ts": t,
    "authentication": au
}
}

//分类
async function home (filter) {
let html = rsaDecrypt((await req(`${host}/app/channel?top-level=true`, {
        headers: gethh()
    })).content)

//一级
let classes = html.data.map(tp => ({
  type_id: tp.id,
  type_name: tp.name
})).filter(item => item.type_name !== "猜你想看");

//二级
let filterObj = {};
for (const item of html.data) {
  filterObj[item.id] = [
    {
      "key": "class",
      "name": "剧情",
      "value": (item.types||'').filter(v => v).map(v => ({ n: v, v }))
    },
    {
      "key": "area",
      "name": "地区",
      "value": (item.areas||'').filter(v => v).map(v => ({ n: v, v }))
    },
    {
      "key": "year",
      "name": "年份",
      "value": (item.years||'').filter(v => v).map(v => ({ n: v, v }))
    },
    {
      "key": "sort",
      "name": "排序",
      "value": [{"n":"最新","v":"addtime"},{"n":"最热","v":"hits"},{"n":"评分","v":"gold"}]
    }
  ];
}

return JSON.stringify({
    class: classes,
    filters: filterObj,
//    list: videos
});
}


//主页推荐
async function homeVod() {
}

//分类
async function category (tid, pg, filter, extend) {
let html = rsaDecrypt((await req(`${host}/app/video/list?channel=${tid}&type=${extend.class || ''}&area=${extend.area || ''}&year=${extend.year || ''}&sort=${extend.sort || 'addtime'}&limit=30&page=${pg}`, {
        headers: gethh()
    })).content)

let videos = html.data.items.map(item => ({
    vod_id: item.id,
    vod_name: item.name,
    vod_pic: item.pic,
    vod_remarks: item.continu,
    vod_year: item.year
}));

return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let html = rsaDecrypt((await req(`${host}/app/video/detail?id=${id}`, {
        headers: gethh()
    })).content).data

let play_from = html.parts.map(item => `${item.play}`).join('$$$').replace(/cn/,'国语').replace(/en/,'英语').replace(/newup-jp/,'日语');
let play_url = html.parts.map(play => {
    let p = play.play
    return play.part.map(item => {
       return `${item}$${id}@@${p}@@${item}`;
    }).join('#');
}).join('$$$');

var vod = {
    "type_name": html.type,
    "vod_year": html.year,
    "vod_area": html.area,
    "vod_remarks": html.continu,
    "vod_actor": html.actor,
    "vod_director": html.director,
    "vod_content": html.content.replace(/&nbsp;/g,''),
    "vod_play_from": play_from.includes('$$$') ? play_from : '动漫共和国APP', 
    "vod_play_url": play_url
    }

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
let ids = id.split('@@')
let html = rsaDecrypt((await req(`${host}/app/video/play?id=${ids[0]}&play=${ids[1]}&part=${ids[2]}`, {
        body: getbody(),
        headers: gethh(),
        method: 'POST'
    })).content)
let url = html.data[0].url.split('-')
if (url[0] == 'new'){
let p = html.data[0].parse
let md5rand = getrand(4)
let aa = url[1].split('').reverse().join('') //反转
let bb = md5X(`${md5rand}:${aa}:UY9kxQEtk8Dn08Kr:J5jQnzGVRfCe4CUk`).slice(0, 2) //取前两位为效验码
let urlmd5 = en(`${md5rand}-${aa.slice(0, 16)}${bb}${aa.slice(16)}`)  // bb校验码加在aa的中间，16位之后

let t = Math.floor(Date.now() / 1000) + 600;
let signrand = getrand(16)
let authrand = getrand(20)
let proofrand = getrand(20)
let checkrand = getrand(20)

let API_AUTH_CONFIG_KEY = p.match(/API_AUTH_CONFIG[\s\S]*?KEY = "(.*?)"/)[1];
let AUTH_KEY = p.match(/API_HEADER_AUTH[\s\S]*?KEY = "(.*?)"/)[1];
let EXTRA_KEY = p.match(/API_HEADER_AUTH[\s\S]*?EXTRA_KEY = "(.*?)"/)[1];
let CHECK_KEY = p.match(/API_HEADER_AUTH[\s\S]*?CHECK_KEY = "(.*?)"/)[1];
let API_ENDPOINT = p.match(/CONSTANTS[\s\S]*?API_ENDPOINT = "(.*?)"/)[1];


let path = '/'  // API_AUTH_CONFIG.NEW.PATH
let sign = `sign=${t}-${signrand}-0-` + md5X(`${path}-${t}-${signrand}-0-${API_AUTH_CONFIG_KEY}`) // API_AUTH_CONFIG.KEY
let auth = `v1:${t}:${authrand}:` + md5X(`${path}|${urlmd5}|${sign}|${t}|${authrand}|${AUTH_KEY}`)  // API_HEADER_AUTH.KEY
let proof = `v1:${proofrand}:` + md5X(`${path}|${urlmd5}|${sign}|${auth}|${proofrand}|${EXTRA_KEY}`)  // API_HEADER_AUTH.EXTRA_KEY
let check = `v1:${checkrand}:` + md5X(`${path}|${urlmd5}|${sign}|${auth}|${proof}|${checkrand}|${CHECK_KEY}`)  // API_HEADER_AUTH.CHECK_KEY
let probe = p.match(/API_HEADER_AUTH[\s\S]*?PROBE_KEY = "(.*?)"/)[1]; // API_HEADER_AUTH.PROBE_KEY

// NEW_API_ENDPOINT
let res = (await req(`${API_ENDPOINT}${urlmd5}&${sign}`, {
  headers: {
    "user-agent": "Dart/3.6 (dart:io)",
    "x-goepp-client-probe": probe,
    "x-goepp-client-auth": auth,
    "x-goepp-client-proof": proof,
    "x-goepp-client-check": check
  }
})).content
let list = [];

JSON.parse(res).data.playAddr.forEach(item => {
  const url = getM3u8(`${de(item.m3u8FileDomain)}${de(item.addr)}`, p);
  const name = item.desc;
  list.push(name, url);
});

return JSON.stringify({ parse: 0, url: list });
}

}

//搜索
async function search (wd, quick, pg=1) {
let html = rsaDecrypt((await req(`${host}/app/video/search?key=${wd}&limit=25&page=${pg}`, {
        headers: gethh()
    })).content)

let videos = html.data.items.map(item => ({
    vod_id: item.id,
    vod_name: item.name,
    vod_pic: item.pic,
    vod_remarks: item.continu,
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