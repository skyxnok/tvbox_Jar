//https://ani.lanerc.app/
import "../lib/node-forge.js";

let host = 'https://lol.jngaoke.cn/';

async function request(reqUrl, body) {
  const opt = { headers: { "User-Agent": "Dart/3.5 (dart:io)", "upgrade-insecure-requests": "1", "Accept": "application/json"} };
  if (body && typeof body === 'object') {
    opt.body = JSON.stringify(body);
    opt.method = 'POST';
    opt.headers['Content-Type'] = 'application/json';
  }
  const res = JSON.parse((await req(reqUrl, opt)).content).data;
  return JSON.parse(de(res));
}

//随机6位字符串
function rand6() {
    var chars = 'abcdefghijklmnopqrstuvwxyz0123456789';
    var s = '';
    for (var i = 0; i < 6; i++) s += chars[Math.floor(Math.random() * chars.length)];
    return s;
}

function getSign(path) {
    var clean = String(path || '').replace(/^\/+/, '');
    var time = Math.floor(Date.now() / 1000);
    var nonce = rand6();
    //  /app/vod/filter@1786440422@490lkj@4x2g5efd84fb46a9  (secret 已更新为最新版)
    var digest = md5X('/' + clean + '@' + time + '@' + nonce + '@' + '4x2g5efd84fb46a9').toLowerCase(); 
    return host + clean + '?sign=' + time + '-' + nonce + '-' + digest;
}

//解密
function de(data) {
    const datas = data.replace(/1/g, '!').replace(/5/g, '@').replace(/9/g, '#').replace(/\//g, '*').replace(/-/g, '&').replace(/!/g, '9').replace(/@/g, '1').replace(/#/g, '5').replace(/\*/g, '+').replace(/&/g, '/');
    const key = '8f81c2519e3b661834219e7142000093';
    return aesX('AES/ECB/PKCS5', false, datas, true, key, null, false);
}

//分类
async function home (filter) {
let html = await request(`${host}app/home`);
//一级
let classes = html.vod_list.map(tp => ({
  type_id: tp.sort_id,
  type_name: tp.sort_name
}))

//二级
let filterObj = {};
for (const item of html.vod_list) {
    const filters = [];
    // type_class 拆分
    const classValues = item.type_class.split(',')
      .map(v => ({ n: v.trim(), v: v.trim() }))
      .filter(item => item.v);
    filters.push({ key: 'class', name: '类型', value: classValues });

    // type_year 拆分
    const yearValues = item.type_year.split(',')
      .map(v => ({ n: v.trim(), v: v.trim() }))
      .filter(item => item.v);
    filters.push({ key: 'year', name: '年份', value: yearValues });
    
    filterObj[item.sort_id] = filters;
}

//推荐
let videos = html.banner?.map(item => ({
    vod_id: item.vod_id,
    vod_name: item.title,
    vod_pic: item.image.indexOf('doubanio.com') === -1 ? item.image : item.image + '@Referer=https://movie.douban.com@User-Agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36',
    style: {"type": "rect", "ratio": 1.485 }
}));

return JSON.stringify({ class: classes, filters: filterObj, list: videos });
}

//推荐
async function homeVod() {

}

//分类
async function category (tid, pg, filter, extend) {
let html = await request(`${host}app/vod/filter?page=${pg}&class_id=${tid}&vod_class=${extend.class || ''}&year=${extend.year || ''}`);

let videos = html.filter_vods.map(item => ({
    vod_id: item.id,
    vod_name: item.vod_name,
    vod_pic: item.vod_pic.indexOf('doubanio.com') === -1 ? item.vod_pic : item.vod_pic + '@Referer=https://movie.douban.com@User-Agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36',
}));

return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let html = await request(`${host}app/getvod/${id}`);

let play_from = html.video_play_list.map(item => item.name).join('$$$');
let play_url = html.video_play_list.map(play => {
  let player = play.player;
  return play.video.map(vid => `${vid}@@${player}`).join('#');
}).join('$$$');

var vod = {
    "type_name": html.video_play_info.vod_class,
    "vod_year": html.video_play_info.vod_year,
    "vod_area": '',
    "vod_remarks": html.video_play_info.vod_remarks,
    "vod_actor": "",
    "vod_director": html.video_play_info.vod_author,
    "vod_content": html.video_play_info.vod_blurb.replace(/<.*?>/g, ''),
    "vod_play_from": play_from.replace(/-首次加载缓慢请耐心等待/g,''), 
    "vod_play_url": play_url
    }

return JSON.stringify({ list: [vod] })
}


// ============ proxyx4x 播放解析（proxyx3x 已过期，改用 proxyx4x） ============
const _PLAY_RESOLVE_PATH = 'app/proxyx4x';
const _PLAY_RESOLVE_KEY_HEX = '5a31fe3201838a69e8f9c135f7905db25208fbc6bc3f0a9b017fc5139a451108';

// 把任意输入（Latin1 字节串 / 字节数组）统一转成整型字节数组，避免 QuickJS 字符串 slice 的 UTF-16 歧义
function _toBytes(value) {
  if (value === null || value === undefined) return [];
  if (typeof value === 'string') {
    const arr = [];
    for (let i = 0; i < value.length; i++) arr.push(value.charCodeAt(i) & 0xff);
    return arr;
  }
  if (typeof value === 'object' && typeof value.length === 'number') {
    const arr = [];
    for (let i = 0; i < value.length; i++) arr.push(Number(value[i]) & 0xff);
    return arr;
  }
  return [];
}

// protobuf 简单字段解析：按 tag/type 切分（基于整型字节数组，与最新版一致）
function _protoFields(input) {
  const bytes = _toBytes(input);
  const fields = {};
  let off = 0;
  function readVarint() {
    let result = 0, shift = 0;
    while (off < bytes.length && shift < 56) {
      const cur = bytes[off++];
      result += (cur & 0x7f) * Math.pow(2, shift);
      if ((cur & 0x80) === 0) return result;
      shift += 7;
    }
    throw new Error('protobuf varint 非法');
  }
  while (off < bytes.length) {
    const tag = readVarint();
    const fieldNo = Math.floor(tag / 8);
    const wireType = tag & 7;
    if (!fieldNo) throw new Error('protobuf field number 非法');
    let val;
    if (wireType === 0) {
      val = readVarint();
    } else if (wireType === 2) {
      const len = readVarint();
      if (len < 0 || off + len > bytes.length) throw new Error('protobuf length 截断');
      val = bytes.slice(off, off + len);
      off += len;
    } else if (wireType === 5) {
      if (off + 4 > bytes.length) throw new Error('protobuf fixed32 截断');
      val = bytes.slice(off, off + 4); off += 4;
    } else if (wireType === 1) {
      if (off + 8 > bytes.length) throw new Error('protobuf fixed64 截断');
      val = bytes.slice(off, off + 8); off += 8;
    } else {
      throw new Error('protobuf wire type 不支持：' + wireType);
    }
    (fields[fieldNo] = fields[fieldNo] || []).push({ wireType, value: val });
  }
  return fields;
}

// AES-GCM 解密（使用 node-forge，cipherBase64 = ciphertext + 末尾16字节tag，nonce 为 base64，key 为 hex）
function _aesGcmDecrypt(cipherBase64, keyHex, nonceBase64) {
  const key = forge.util.createBuffer().putBytes(forge.util.hexToBytes(keyHex));
  const iv = forge.util.createBuffer().putBytes(forge.util.decode64(nonceBase64));
  const cipherBytes = forge.util.decode64(cipherBase64);
  const decipher = forge.cipher.createDecipher('AES-GCM', key);
  decipher.start({ iv: iv, tag: forge.util.createBuffer().putBytes(cipherBytes.slice(-16)) });
  decipher.update(forge.util.createBuffer().putBytes(cipherBytes.slice(0, -16)));
  decipher.finish();
  return forge.util.encode64(decipher.output.getBytes());
}

// 字节数组 <-> base64 / utf8 统一走 node-forge，不再手写
function _b64ToBytes(b64) {
  return forge.util.decode64(b64);
}
function _utf8Decode(bytes) {
  return forge.util.decodeUtf8(_bytesToLatin1(bytes));
}

// 整型字节数组 -> Latin1 字符串（forge 的 decode/encode64 期望 Latin1 字节串）
function _bytesToLatin1(bytes) {
  let s = '';
  for (let i = 0; i < bytes.length; i++) s += String.fromCharCode(bytes[i] & 0xff);
  return s;
}

async function _resolveProxyX4X(vid, pid, sign, auth) {
  const body = { vid, player: pid, sign, auth };
  // proxyx4x 返回 protobuf 二进制，使用带签名的 POST
  // buffer: 2 让框架返回 base64 字符串，避免默认按 UTF-8 解码破坏二进制
  // UA 与 accept-encoding 必须与官方一致（Dart/3.9.2 + gzip），否则服务器返回"版本过旧"提示片
  const opt = {
    method: 'POST',
    buffer: 2,
    ua: "Dart/3.9.2",
    headers: { "User-Agent": "Dart/3.9.2", "Content-Type": "application/json", "Accept": "application/x-protobuf", "Accept-Encoding": "gzip" },
    body: JSON.stringify(body)
  };
  const signUrl = getSign(_PLAY_RESOLVE_PATH);
  const res = await req(signUrl, opt);
  const content = res && res.content;
  // buffer: 2 返回 base64 字符串，先用 forge 解码成 Latin1 字节串再交给 protobuf 解析
  const contentBytes = forge.util.decode64(String(content || ''));
  // 诊断日志：打印 content 真实类型、长度、前若干字节（十六进制）以及解析出的 field 键
  try {
    const raw = _toBytes(contentBytes);
    let hex = '';
    for (let i = 0; i < Math.min(raw.length, 48); i++) hex += ('0' + raw[i].toString(16)).slice(-2) + ' ';
    console.log('[proxyx4x] content type=' + (typeof content) + ' len=' + raw.length + ' headHex=' + hex.trim());
  } catch (e) { console.log('[proxyx4x] diag err ' + e.message); }
  const envelope = _protoFields(contentBytes);
  console.log('[proxyx4x] envelope fields=' + JSON.stringify(Object.keys(envelope)));
  const encField = envelope[4] && envelope[4][0];
  if (!encField || encField.wireType !== 2) throw new Error('proxyx4x 响应缺少加密字段');
  const encBytes = encField.value;
  if (encBytes.length < 12 + 16) throw new Error('proxyx4x 密文长度非法');
  const nonce = encBytes.slice(0, 12);
  const cipherAndTag = encBytes.slice(12);
  const cipherB64 = _bytesToB64(cipherAndTag);
  const plainB64 = _aesGcmDecrypt(cipherB64, _PLAY_RESOLVE_KEY_HEX, _bytesToB64(nonce));
  const plainBytes = _b64ToBytes(plainB64);
  const inner = _protoFields(plainBytes);
  const urlField = inner[1] && inner[1][0];
  if (!urlField || urlField.wireType !== 2) throw new Error('proxyx4x 解密结果缺少播放地址');
  return _utf8Decode(urlField.value);
}
function _bytesToB64(bytes) {
  return forge.util.encode64(_bytesToLatin1(bytes));
}

// ===== 防盗提示片检测 + 官方镜像回退（对齐最新版 play 逻辑） =====
const _LANERC_OFFICIAL_MEDIA_HOSTS = ['https://file.shangji.asia', 'http://static.shangji.asia'];
const _LANERC_OFFICIAL_MEDIA_BUCKETS = ['10', '13', '2', '8'];

// 下载 m3u8 文本（原生 req，不解密 .data）
function _fetchText(url) {
  const opt = { headers: { "User-Agent": "Dart/3.9.2", "Accept": "*/*" } };
  const res = req(url, opt);
  return res && res.content ? String(res.content) : '';
}

// 返回 1=提示片, 0=正常/未识别, -1=无法判定
function _inspectLanercPlaylist(url) {
  const value = String(url || '');
  if (!/^https?:\/\//i.test(value)) return -1;
  if (!/\.m3u8(?:$|[?#])/i.test(value)) return 0;
  try {
    const playlist = String(_fetchText(value) || '');
    if (!/^#EXTM3U/m.test(playlist)) return -1;
    const pattern = /#EXTINF:\s*([0-9]+(?:\.[0-9]+)?)/ig;
    let count = 0, duration = 0, matched;
    while ((matched = pattern.exec(playlist)) !== null) { count += 1; duration += Number(matched[1]); }
    const lines = playlist.split(/\r?\n/);
    let segmentCount = 0, disguisedSegmentCount = 0;
    for (let i = 0; i < lines.length; i++) {
      const line = (lines[i] || '').trim();
      if (!line || line.charAt(0) === '#') continue;
      segmentCount += 1;
      if (/\.(?:png|jpe?g|webp|gif)(?:$|[?#])/i.test(line)) disguisedSegmentCount += 1;
    }
    if (segmentCount < 1) return -1;
    // 约 180s 或 240s 且带图片伪装的为提示片
    if (count >= 10 && duration >= 179 && duration <= 181) return 1;
    if (count >= 10 && duration >= 239 && duration <= 241 && disguisedSegmentCount > 0) return 1;
    return 0;
  } catch (e) {
    console.log('[proxyx4x] 提示片检测失败：' + e.message);
    return -1;
  }
}

function _resolveLanercOfficialMedia(vid) {
  const episodeId = String(vid || '').trim().toLowerCase();
  if (!/^[0-9a-f]{32}$/.test(episodeId)) return '';
  for (let h = 0; h < _LANERC_OFFICIAL_MEDIA_HOSTS.length; h++) {
    let hostUrl = String(_LANERC_OFFICIAL_MEDIA_HOSTS[h]).replace(/\/+$/, '');
    if (!/^https?:\/\//i.test(hostUrl)) continue;
    for (let b = 0; b < _LANERC_OFFICIAL_MEDIA_BUCKETS.length; b++) {
      const bucket = String(_LANERC_OFFICIAL_MEDIA_BUCKETS[b]).replace(/^\/+|\/+$/g, '');
      if (!bucket) continue;
      const url = hostUrl + '/' + bucket + '/' + episodeId + '.m3u8';
      try {
        if (_inspectLanercPlaylist(url) === 0) return url;
      } catch (e) { /* 跳过 */ }
    }
  }
  return '';
}

//播放（proxyx4x，proxyx3x 已过期）
async function play (flag, id, flags) {
let [vid, pid] = id.split('@@');

// 优先尝试官方镜像（实测 proxyx4x 当前稳定返回防盗提示片）
const official = _resolveLanercOfficialMedia(vid);
if (official) {
  console.log('[proxyx4x] 命中官方镜像：' + official);
  return JSON.stringify({parse: 0, url: official});
}

// 官方镜像未命中再走 proxyx4x
let data = {
  "vid": vid,
  "player": pid,
  "sign": "74322D4D62B9F4A986DFA8973EE70EBC034E74551B8715C755EDD9ED18E6820B",
  "auth": "com.clggjv.xcjfmd.ffo"
}
let url = await _resolveProxyX4X(vid, pid, data.sign, data.auth);

// 对齐最新版：检测防盗提示片
if (/\.m3u8(?:$|[?#])/i.test(url) && _inspectLanercPlaylist(url) === 1) {
  return JSON.stringify({url: '', type: 'auto', error: '检测到防盗提示片，当前线路不可播放'});
}

return JSON.stringify({parse: 0, url: url})
}

//搜索
async function search (wd, quick, pg=1) {
let html = await request(`${host}app/vod/search?keyword=${wd}`);

let videos = html.search_vods.map(item => ({
    vod_id: item.id,
    vod_name: item.vod_name,
    vod_pic: item.vod_pic.indexOf('doubanio.com') === -1 ? item.vod_pic : item.vod_pic + '@Referer=https://movie.douban.com@User-Agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36',
}));

return JSON.stringify({limit: videos.length, list: videos});
}

export function __jsEvalReturn() {
  return {
//      init: init,
      home: home,
      homeVod: homeVod,
      category: category,
      detail: detail,
      play: play,
      search: search
  };
}

