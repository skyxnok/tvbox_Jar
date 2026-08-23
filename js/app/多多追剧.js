//a.sb.j.Sbn.c   sk
//a.sb.j.Sbn.d   finger
//a.sb.j.Sbn.f   解密（AES-128-GCM，密文=[12字节IV][密文][16字节GCM tag]，key 索引由响应头 mcg821-a 下发）


//https://duoduozhuiju.com
let host, finger, sk, ave, avn, aid;

const RESP_KEYS = [
    "A7mQ9vL2pX4rZ8tN", "b3Tn6Yq8Kp2Vx5Ls", "R5cH2wN9eM7qP4vD", "x8Lk1Zp6Cw3Nq9Ty",
    "M4vS7rQ2bT9hX6kE", "p9Dq3Lx8Vn5Cz2Ra", "K2wF6tM8yQ4sH7Np", "z6Pj9Rb3Lc8Vx1Tm",
    "N8qC4yL7pS2dK5Wa", "t3Vx9Mn6Qp4Rs8Yk", "C7hL2qT5vN9xB3Wp", "y4Rk8Pz1Md6Lq3Vs",
    "L9pX2cQ7tV4nR8Hy", "q5Nw8Zr3Kp6Mt2Va", "V2cT9yL5Rq8Nw4Ks", "s8Kp4Xn7Cw2Vq9Md",
    "D6rM1tY8pL3zQ5Vx", "w9Qv5Ck2Nr8Ty4Lp", "P3xL7mR9qV2cN6Ty", "h2Zq8Vn4Kp7Sx5Mc",
    "T8mC3yQ6rL9pV2Nw", "n5Rk2Pz8Xq4Vt7Ls", "Q4vN9cL3Mp6Yx2Rk", "k7Tq1Wv5Zn8Pc4Ms",
    "X6pL3rV8Cq2Ny9Kt", "m2Qz7Kp4Vx9Ts5Rc", "Y9cV5nL2Rq8Pw3Kx", "r4Mp8Tq1Zc6Vn9Ly",
    "B7xQ2vK9pR5Lm3Ts", "u8Lr4Cq7Nw2Vp5Yz"
];
function decryptResponse(body, keyIndex) {
    const key = RESP_KEYS[parseInt(keyIndex, 10)];
    if (!key) return body;
    return aesXS("AES/GCM", false, body, "hex", key, "utf8", "", "", "utf8");
}


async function init(cfg) {
host = cfg.ext?.host || "https://bubutv.top"
finger = cfg.ext?.finger || "SF-C3B2B41F6EFFFF9869176CF68F6790E8F07506FC88632C94B4F5F0430D5498CA"
sk = cfg.ext?.sk || "SK-random"
ave = cfg.ext?.ave || "8"
avn = cfg.ext?.avn || "1.6.1"
aid = cfg.ext?.aid || "com.sunshine.tv"
}

//统一请求
async function request(reqUrl, body) {
    let t = Date.now();
    let nonc = Array.from({length: 16}, () => "0123456789ABCDEF"[Math.floor(Math.random() * 16)]).join("");
//    let sign = CryptoJS.SHA256(`finger=SF-C3B2B41F6EFFFF9869176CF68F6790E8F07506FC88632C94B4F5F0430D5498CA&id=com.sunshine.tv&nonce=${nonc}&sk=SK-random&time=${t}&v=8`).toString().toUpperCase();
    let sign = sha256X(`finger=${finger}&id=${aid}&nonce=${nonc}&sk=${sk}&time=${t}&v=${ave}`, false, false).toUpperCase();
    const opt = {
        headers: {
            "x-aid": aid,
            "x-ave": ave,
            "x-avn": avn,
            "x-time": t,
            "x-nonc": nonc,
            "x-sign": sign,
            "accept": "application/json",
            "x-device-id": "68babf529c1e02ba",
            "x-device-brand": "Xiaomi",
            "x-device-model": "23116PN5BC",
            "x-update-id": "7a24e3d3-c8f9-109a-0ce3-67e6bd094e01",
            "user-agent": "okhttp/4.12.0"
        },
        buffer: 1 // 返回原始字节数组，避免二进制被当 UTF-8 字符串解码成乱码
    };
    if (body && typeof body === 'object') {
        opt.body = JSON.stringify(body);
        opt.method = 'POST';
    }
    let resp = await req(reqUrl, opt);
    let content = resp.content;
    // buffer:1 时 content 为原始字节数组，转 hex；响应头 mcg821-a 为 key 索引
    const keyIndex = resp.headers?.["mcg821-a"] ?? resp.headers?.["Mcg821-A"] ?? "0";
    if (Array.isArray(content)) {
        content = content.map(b => (b & 0xff).toString(16).padStart(2, '0')).join('');
    }
    content = decryptResponse(content, keyIndex);
    return JSON.parse(content);
}


//分类
async function home (filter) {
let html = await request(`${host}/api.php/app/index/home`)

let classes = html.data.categories.map(tp => ({
  type_id: tp.type_name,
  type_name: tp.type_name
}))

let filterObj = {};
for (const item of classes) {
  filterObj[item.type_id] = [
    {
      "key": "sort",
      "name": "排序",
      "value": [{"n": "人气", "v": "hits"},{"n": "最新", "v": "time"},{"n": "评分", "v": "score"},{"n": "年份", "v": "year"}]
    }
  ];
}
let videos = html.data.recommend.map(item => ({
    vod_id: item.vod_id,
    vod_name: item.vod_name,
    vod_pic: item.vod_pic,
    vod_remarks: item.vod_remarks
}));

return JSON.stringify({ class: classes, filters: filterObj, list: videos });
}


//主页推荐
async function homeVod() {
}

//分类
async function category (tid, pg, filter, extend) {
let html = await request(`${host}/api.php/app/filter/vod?type_name=${tid}&page=${pg}&sort=${extend.sort || 'hits'}`)

let videos = html.data.map(item => ({
    vod_id: item.vod_id,
    vod_name: item.vod_name,
    vod_pic: item.vod_pic,
    vod_remarks: item.vod_remarks,
    vod_year: item.vod_year
}));

return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let html = await request(`${host}/api.php/app/vod/get_detail?vod_id=${id}`)
let cj = await request(`${host}/api.php/app/internal/search_aggregate?vod_id=${id}`)
let cjfrom = cj.data.map(item => item.site_name).join('$$$');
let cjurl = cj.data.map(item => item.vod_play_url).join('$$$');

let play = html.vodplayer.sort((a, b) => {
    const getRank = (item) => {
        const f = item.from.toLowerCase();
        if (f.includes('4k')) return 1;
        if (f.includes('2k')) return 2;
        if (f.includes('臻彩') || f.includes('真彩')) return 3;
        return 99;
    };

    const rankA = getRank(a);
    const rankB = getRank(b);

    if (rankA !== rankB) {
        return rankA - rankB;
    }
    // 同档位 sort 从小到大
    return Number(a.sort) - Number(b.sort);
});

let res = html.data[0]
let fromArr = res.vod_play_from.split('$$$');
let urlArr = res.vod_play_url.split('$$$');
let play_from_arr = [];
let play_url_arr = [];

for (let p of play) {
    let from = p.from;
    let show = p.show;
    // 找到原始分组下标
    let idx = fromArr.indexOf(from);
    if (idx === -1) continue;
    play_from_arr.push(`${show} [${from}]`);
    let urls = urlArr[idx] || '';
    let url_group = urls.split('#').map(u => `${u}@@${from}`).join('#');
    play_url_arr.push(url_group);
}

let play_from = play_from_arr.join('$$$');
let play_url = play_url_arr.join('$$$');

var vod = {
    "type_name": res.vod_class,
    "vod_year": res.vod_year,
    "vod_area": res.vod_area,
    "vod_remarks": res.vod_remarks,
    "vod_actor": res.vod_actor,
    "vod_director": res.vod_director,
    "vod_content": res.vod_content.replace(/<.*?>/g, ''),
    "vod_play_from": play_from+'$$$'+cjfrom,
    "vod_play_url": play_url+'$$$'+cjurl
}

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
if (id.indexOf("@@") > -1){
let [ids, vodFrom] = id.split('@@')
let url = (await request(`${host}/api.php/app/decode/url/?url=${encodeURIComponent(ids)}&vodFrom=${vodFrom}`)).data

return JSON.stringify({ parse: 0,url: url })
}

return JSON.stringify({ parse: 0,url: id })
}

//搜索
async function search (wd, quick, pg=1) {
let html = await request(`${host}/api.php/app/search/index?wd=${wd}&page=${pg}&limit=15`)

let videos = html.data.map(item => ({
    vod_id: item.vod_id,
    vod_name: item.vod_name,
    vod_pic: item.vod_pic,
    vod_remarks: item.vod_remarks,
    vod_year: item.vod_year
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