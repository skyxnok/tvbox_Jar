let host, config;

//vip1948.com
async function init(cfg) {
host = cfg.ext?.host || "https://www.cd-zj.com"
config = JSON.parse((await request(`${host}/static/js/playerconfig.js`)).match(/player_list=(.*?),MacP/)?.[1])

}

async function request(reqUrl) {
  const res = await req(reqUrl, {
    method: 'get',
    headers: {
      'User-Agent': 'Mozilla/5.0 (Linux; Android 9; SHARK PRS-A0 Build/PQ3B.190801.12191711) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Mobile Safari/537.36',
      'Referer': host
    }
  });
  return res.content;
}

//解析
function getlist(html, startFlag, closeTag) {
  const start = html.indexOf(startFlag);
  if (start === -1) return null;

  const openTag = startFlag.match(/^<[a-zA-Z0-9]+/)[0];
  let count = 1, pos = start + startFlag.length;

  while (count > 0) {
    const nextOpen = html.indexOf(openTag, pos);
    const nextClose = html.indexOf(closeTag, pos);
    if (nextClose === -1) return null;

    if (nextOpen !== -1 && nextOpen < nextClose) {
      count++;
      pos = nextOpen + openTag.length;
    } else {
      count--;
      pos = nextClose + closeTag.length;
      if (count === 0) return html.substring(start, pos);
    }
  }
}


//分类
async function home (filter) {
//一级
let classes = [{"type_id":"/label/qq","type_name":"腾讯精选"},{"type_id":"/label/bli","type_name":"哔哩精选"},{"type_id":"/label/youku","type_name":"优酷精选"},{"type_id":"2","type_name":"剧集"},{"type_id":"1","type_name":"电影"},{"type_id":"4","type_name":"动漫"},{"type_id":"3","type_name":"综艺"},{"type_id":"5","type_name":"短剧"}]

let filterObj = {"1":[{"key":"sort","name":"sort","value":[{"n":"人气","v":"hits"},{"n":"评分","v":"score"}]}],"2":[{"key":"sort","name":"sort","value":[{"n":"人气","v":"hits"},{"n":"评分","v":"score"}]}],"3":[{"key":"sort","name":"sort","value":[{"n":"人气","v":"hits"},{"n":"评分","v":"score"}]}],"4":[{"key":"sort","name":"sort","value":[{"n":"人气","v":"hits"},{"n":"评分","v":"score"}]}],"5":[{"key":"sort","name":"sort","value":[{"n":"人气","v":"hits"},{"n":"评分","v":"score"}]}]}

return JSON.stringify({ class: classes, filters: filterObj });
}


//主页推荐
async function homeVod() {
}

//分类
async function category (tid, pg, filter, extend) {
let url = tid.includes('/') ? `${host}${tid}/page/${pg}.html` : `${host}/cupfox-list/${tid}--${extend.sort || 'time'}------${pg}---.html`;
let html = await request(url);

let res = getlist(html, '<div class="box-width wow', '</div>')
let regex = /<img.*?data-src="(.*?)"[\s\S]*?<i.*?>(.*?)<[\s\S]*?<a.*?href="(.*?)" title="(.*?)">/g;
let videos = [];
let match;

while ((match = regex.exec(res)) !== null) {
    videos.push({
      vod_id: match[3],
      vod_name: match[4],
      vod_pic: match[1].replace(/&amp;/g,'&'),
      vod_remarks: match[2],
//      vod_year: ''
    });
}

return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let html = await request(`${host}${id}`);
let res = getlist(html, '<div class="anthology wow fadeInUp animated"', '</div>')

// ========== 1. 提取所有播放线路名称 ==========
let play_from = [];
let fromName = /<\/i>(.*?)</g; //线路名
let match;
// 循环匹配每一条线路名
while ((match = fromName.exec(res))) {
    let Name = match[1].trim().replace(/&nbsp;/g, '');
    play_from.push(Name);
}

// ========== 2. 提取所有线路的集数播放链接 ==========
let play_url = [];
let line = /<ul.+?>[\s\S]*?<\/ul>/g; //线路
let epreg = /href="(.*?)".*?>(.*?)</g; //href 链接与集数标题
let lineMatch;
// 遍历每一条播放线路
while ((lineMatch = line.exec(res))) {
    let ep = [];
    let epMatch;
    
    while ((epMatch = epreg.exec(lineMatch[0]))) {
        ep.push(`${epMatch[2].replace(/\b0+(?=[1-9])/g, '')}$${epMatch[1]}`);
    }
    //play_url.push(ep.join('#'));
    //倒序
    play_url.push(ep.reverse().join('#'));
}

var vod = {
    "type_name": '',
    "vod_year": '',
    "vod_area": '',
    "vod_actor": '',
    "vod_director": '',
    "vod_remarks": '',
    "vod_content": html.match(/<div id="height_limit".*?>([\s\S]*?)</)?.[1]?.replace(/&amp;|&nbsp;/g,'&') || '',
    "vod_play_from": play_from.join('$$$'), 
    "vod_play_url": play_url.join('$$$')
    }

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
let html = await request(`${host}${id}`);
let res = JSON.parse(html.match(/var player_aaaa=(.*?)</)[1])

if (!res.url.includes('m3u8')){
let jxhost = config[res.from].parse
let phost = jxhost.split('/')[2]
let res2 = await request(`${jxhost}${res.url}`)
let token = res2.match(/data-te="(.*?)"/)?.[1]
let playurl = JSON.parse((await req(`https://${phost}/player/mplayer.php`, {
    body: `url=${res.url}&token=${token}`,
    headers: {"Content-Type":"application/x-www-form-urlencoded; charset=UTF-8"},
    method: 'POST'
})).content).url
return JSON.stringify({ parse: 0, url: playurl })
}
return JSON.stringify({ parse: 0, url: res.url })
}

//搜索
async function search (wd, quick, pg=1) {
let html = await request(`${host}/index.php/ajax/suggest?mid=1&wd=${wd}&limit=500`);
let videos = JSON.parse(html).list.map(item => ({
    vod_id: `/detail/${item.id}.html`,
    vod_name: item.name,
    vod_pic: item.pic.replace(/&amp;/g,'&')
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