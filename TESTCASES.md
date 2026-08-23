# 新写 Spider 测试用例与返回数据格式

> 依据：TVBox Spider 接口约定，返回格式参考已存在并验证过的老蜘蛛（`Jpyy.java`、`AppYsV2.java` 等）。
> 状态：仅保证编译通过，**未在真实 App 中逐源实测**，本文件用于逐源验收。

## 0. 通用返回格式标准（参考老蜘蛛）

| 方法 | 返回 JSON 结构 |
| --- | --- |
| `homeContent(boolean filter)` | `{"class":[{"type_id","type_name"}],"filters":{type_id:[{key,name,value}]}}` |
| `homeVideoContent()` | `{"list":[{"vod_id","vod_name","vod_pic","vod_remarks","vod_year"}]}` |
| `categoryContent(tid,pg,filter,extend)` | `{"page":n,"pagecount":n,"limit":n,"total":n,"list":[{"vod_id","vod_name","vod_pic","vod_remarks","vod_year"}]}` |
| `detailContent(List ids)` | `{"list":[{"vod_id","vod_name","vod_pic","type_name","vod_year","vod_area","vod_remarks","vod_actor","vod_director","vod_content","vod_play_from","vod_play_url"}]}` |
| `playerContent(flag,id,vipFlags)` | 标准：`{"parse":0,"playUrl":"","header":"","url":"..."}`；带弹幕：`{"parse":0,"url":"...","danmaku":"..."}`；转解析：`{"parse":1,"url":"...","flag":"..."}` |
| `searchContent(key,quick)` | `{"limit":n,"list":[{"vod_id","vod_name","vod_pic","vod_remarks","vod_year"}]}` |

说明：
- 线路分隔：多线路 `vod_play_from` 用 `$$$`，每线路集数用 `#`，集内 名称$地址 用 `$`。
- 图片防盗链：`vod_pic` 可带 `@Referer=xxx@User-Agent=xxx` 后缀。
- 播放链接里 `@@` 通常表示"ID 分隔符"，由各蜘蛛自行 split 解析。

---

## 1. AiZhan（爱站）— AiZhan.java

- HOST：`https://m3u8.girigirilove.com`
- 测试用例：
  - `categoryContent("1","1",false,{})` → 请求 `{HOST}/api.php/Vod/get_list?offset=0&limit=20&type_id=1` → 返回 `{"page":"1","pagecount":99999,"limit":20,"total":99999,"list":[...]}`
  - `detailContent(["123"])` → 返回 `{"list":[{vod_id,vod_name,vod_pic,type_name,vod_year,vod_area,vod_remarks,vod_actor,vod_director,vod_content,vod_play_from,vod_play_url}]}`
  - `playerContent("","id",[])` → `{"parse":0,"url":id,"danmaku":"弹幕内容"}`
  - `searchContent("海贼王",false)` → `{"limit":n,"list":[...]}`
- 关键字段：`vod_play_from` 把 `chs/cht` 替换为 `简体/繁体`。

## 2. BiliHeji（哔哩合集）— BiliHeji.java

- HOST：`BiliCommon.HOST = https://api.bilibili.com`
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":{}}`
  - `categoryContent(tid,pg,false,{})` → 返回 `{"page":pg,"pagecount":99999,"limit":n,"list":[...]}`
  - `detailContent(["BV1xx"])` → `{"list":[{...,"vod_play_from":"哔哩","vod_play_url":"1$url#2$url"}]}`
  - `playerContent("哔哩","bvid$cid",[])` → `{"header":{"User-Agent":...,"Referer":...},"parse":0,"url":"本地代理?do=bili&bvid=...&cid=...","danmaku":"https://api.bilibili.com/x/v1/dm/list.so?oid=...","format":"application/dash+xml"}`
  - `searchContent("关键词",false)` → `{"limit":n,"list":[...]}`
- 注意：播放 URL 走 `Proxy do=bili` 生成 MPD。

## 3. BiliYingshi（哔哩影视-官方）— BiliYingshi.java

- HOST：`BiliCommon.HOST`；播放/详情走 `https://api.bilibili.com/pgc/...`
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":FILTER_JSON,"list":[...]}`
  - `categoryContent(tid,pg,false,{})` → `{"page":pg,"pagecount":99999,"limit":n,"list":[...]}`
  - `detailContent(["ep123456"])` → 返回含 `vod_play_from:"哔哩"`、`vod_play_url:"1$...|..."`
  - `playerContent(...)` 多分支：
    - 正常：`{"header":{...},"parse":0,"url":mpd代理,"danmaku":...,"format":"application/dash+xml"}`
    - 需解析：`{"header":{...},"parse":0,"url":aes解密后的地址,"danmaku":...}`
    - 兜底：`{"parse":0,"url":link,"jx":1,"danmaku":...}`
  - `searchContent(key,false)` → `{"limit":n,"list":[...]}`
- 注意：存在 AES 解密（`aesDecrypt`）和 `jx=1` 标记。

## 4. BiliYinyue（哔哩音乐）— BiliYinyue.java（extends BiliHeji）

- 仅覆写 `homeContent`，其余复用 `BiliHeji`。
- `homeContent(true)` → `{"class":[...],"filters":FILTER_JSON}`
- 其余用例同 BiliHeji。

## 5. CiYuanCheng（次元城）— CiYuanCheng.java

- HOST：`host` 动态（init 时从配置获取）
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":{...},"list":[...]}`
  - `categoryContent(tid,pg,false,{})` → `{"page":pg,"pagecount":99999,"limit":n,"total":99999,"list":[...]}`
  - `detailContent(["id"])` → 详情请求 `{host}/videos/{id}/sections?...` → 标准 vod 结构
  - `playerContent(flag,id,[])` → `{"parse":0,"url":data.url}`
  - `searchContent(key,false)` → 请求 `{host}/videos/search?q=...` → `{"limit":n,"list":[...]}`
- 注意：详情/播放含分页与 `player_code` 参数。

## 6. DongManGongHeGuo（动漫共和国）— DongManGongHeGuo.java

- HOST：`http://bljhm.xn--vhqr42drhf5k7b.com`
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":{...}}`
  - `categoryContent(tid,pg,false,{})` → 请求 `{host}/app/video/list?channel=...&type=...&area=...&year=...` → `{"page":pg,"pagecount":99999,"limit":n,"total":99999,"list":[...]}`
  - `detailContent(["id"])` → 标准 vod 结构
  - `playerContent(flag,id,[])`：多线路 → `{"parse":0,"url":list}`；单线路 → `{"parse":0,"url":first.url}`
  - `searchContent(key,false)` → `{"limit":n,"list":[...]}`
- 注意：playerContent 有 `parse` 字段判断（`first.optString("parse")`）。

## 7. DuBoKu（独播库）— DuBoKu.java

- HOST：`https://api.dbokutv.com`
- 测试用例：
  - `categoryContent(tid,pg,false,{})` → 请求 `{HOST}/vodshow/{tid}-{area}-{sort}...` → 标准分页结构
  - `detailContent(["id"])` → 标准 vod 结构
  - `playerContent(flag,id,[])` → `{"parse":0,"url":decode(HId)}`（HId 需解码）
  - `searchContent(key,false)` → 请求 `{HOST}/vodsearch?wd=...`（带 sign）→ `{"list":[...]}`
- 注意：搜索需要 `getsign` 生成签名。

## 8. DuoDuoZhuiJu（多多追剧）— DuoDuoZhuiJu.java

- HOST：`https://bubutv.top`
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":{sort:[...]},"list":[...]}`
  - `categoryContent(tid,pg,false,{})` → `{"page":pg,"pagecount":99999,"limit":n,"list":[...]}`
  - `detailContent(["id"])` → `{"list":[{...,"vod_play_from":"线路1$$$解析","vod_play_url":"...$$$..."}]}`（双线路）
  - `playerContent(flag,id,[])` → 解析成功 `{"parse":0,"url":res.data}`，否则 `{"parse":0,"url":id}`
  - `searchContent(key,false)` → `{"limit":n,"list":[...]}`
- 注意：`$$$` 双线路，第二条为聚合解析。

## 9. FengYe4K（枫叶4K）— FengYe4K.java

- HOST：`https://www.cd-zj.com`
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":{...},"list":[...]}`
  - `categoryContent(...)` → `{"page":pg,"pagecount":99999,"limit":n,"total":99999,"list":[...]}`
  - `detailContent(["id"])` → 标准 vod 结构（type_name/year/area/actor/director/remarks 为空串）
  - `playerContent(flag,id,[])` → `{"parse":0,"url":url}`
  - `searchContent(key,false)` → `{"limit":n,"list":[...]}`

## 10. GuaZiYingShi（瓜子影视）— GuaZiYingShi.java

- HOST：`https://api.36kzbh85.com`
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":FILTER_JSON}`
  - `categoryContent(...)` → `{"page":pg,"pagecount":99999,"limit":n,"list":[...]}`
  - `detailContent(["id"])` → 请求 `{host}/App/IndexPlay/playInfo` → `{"list":[{...,"vod_play_from":"瓜子","vod_play_url":"..."}]}`
  - `playerContent(flag,id,[])` → `{"parse":0,"url":html.url,"header":{"User-Agent":"Lavf/57.83.100","Referer":"http://WJiZxLXA2.com/"}}`
  - `searchContent(key,false)` → `{"limit":n,"list":[...]}`
- 注意：播放需固定 UA/Referer。

## 11. HanXiaoQuan（韩小圈）— HanXiaoQuan.java

- HOST：`https://hxqapi.hiyun.tv`
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":{class,year},"list":[...]}`
  - `categoryContent(tid,pg,false,{})` → `{"page":pg,"pagecount":99999,"limit":n,"total":99999,"list":[...]}`
  - `detailContent(["sid"])` → 标准 vod 结构
  - `playerContent(flag,id,[])` → `{"parse":0,"url":"本地代理?do=hanju&pid=...&sq=..."}`
  - `searchContent(key,false)` → `{"limit":n,"list":[...]}`
- 注意：播放走 `Proxy do=hanju`（返回 m3u8）；存在解密 `s.de()` 和激励视频 `traceId` 逻辑。

## 12. HeMaDuanJu（河马短剧）— HeMaDuanJu.java

- SITE：`https://www.kuaikaw.cn`
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"list":[...]}`（首页列表来自 `homeVideoContent`）
  - `categoryContent(...)` → `{"list":[...],"page":n,"pagecount":n,"limit":n,"total":n}`
  - `detailContent(["id"])` → `{"list":[{...,"vod_play_from":"河马剧场","vod_play_url":"ep1$xxx#ep2$yyy"}]}`
  - `playerContent(flag,"/drama/xxx$章节",[])` → `{"parse":0,"url":id,"header":{...}}`，若是直链则直接返回，否则解析 `{site}/episode/...` 找 mp4 后更新 `url`
  - `searchContent(key,false)` → `{"list":[...],"page":"1","pagecount":n,"limit":n,"total":n}`
- 注意：播放 id 格式 `dramaId$chapterId`；依赖 HTML 正则抓 mp4。

## 13. JianPian（荐片）— JianPian.java

- HOST：`https://japi.zxfmj.com`（ext 可覆盖 `host`）
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":FILTER_JSON,"list":[...]}`
  - `categoryContent(...)` → `{"page":pg,"pagecount":99999,"limit":n,"total":99999,"list":[...]}`
  - `detailContent(["id"])` → 标准 vod 结构；播放线路用 `line.url`
  - `playerContent(flag,id,[])` → `{"parse":0,"url":sb}` 或标准结构
  - `searchContent(key,false)` → `{"limit":n,"list":[...]}`
- 注意：图片域名动态（`imghost` 拼接）。

## 14. JinPaiYingYuan（金牌影院）— JinPaiYingYuan.java

- HOST：`https://www.hskjjglo.com`
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":FILTER_JSON}`
  - `homeVideoContent()` → 请求 `{HOST}/api/mw-movie/anonymous/home/hotSearch?` → `{"list":[...]}`
  - `categoryContent(...)` → `{"page":pg,"pagecount":99999,"limit":n,"total":99999,"list":[...]}`
  - `detailContent(["id"])` → 请求 `{HOST}/api/mw-movie/anonymous/video/detail?id=...` → 标准 vod 结构
  - `playerContent(flag,"vid@@nid",[])` → `{"parse":0,"url":[分辨率名,url,...]}`（JSONArray 多清晰度）
  - `searchContent(key,false)` → `{"limit":n,"list":[...]}`
- 注意：player 返回的是**数组**形式的 url（多清晰度），与常规字符串不同。

## 15. JiuJiuYingShi（99影视）— JiuJiuYingShi.java

- HOST：`host` 由 ext 提供（**需用户在配置中填真实 host**）
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":{...}}`
  - `categoryContent(tid,pg,false,{})` → 请求 `{host}/vod/search`（POST `{kw,page,limit,pid}`）→ `{"page":pg,"pagecount":99999,"limit":n,"total":99999,"list":[...]}`
  - `detailContent(["id"])` → 请求 `{host}/vod/detail` → 标准 vod 结构
  - `playerContent(flag,id,[])` → `{"parse":0,"url":url}`
  - `searchContent(key,false)` → 请求 `{host}/vod/search` → `{"limit":n,"list":[...]}`
- 注意：appkey/versionName 等参数为占位，**需真实值**，否则接口会拒。

## 16. KuaiYing4K（快映4K）— KuaiYing4K.java

- HOST：`https://www.kanzurm65ak.top`
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":{...},"list":[...]}`
  - `categoryContent(...)` → `{"page":pg,"pagecount":99999,"limit":n,"total":99999,"list":[...]}`
  - `detailContent(["id"])` → 标准 vod 结构（actor/director 空串）
  - `playerContent(flag,id,[])` → `{"parse":0,"url":url}`
  - `searchContent(key,false)` → `{"limit":n,"list":[...]}`

## 17. Lanerc（Lanerc）— Lanerc.java

- HOST：`https://lol.jngaoke.cn/`
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":{...},"list":[...]}`
  - `categoryContent(...)` → `{"page":pg,"pagecount":99999,"limit":n,"list":[...]}`
  - `detailContent(["id"])` → 标准 vod 结构（`vod_play_from` 会去掉"-首次加载缓慢请耐心等待"）
  - `playerContent(flag,id,[])`：
    - 官方线路 → `{"parse":0,"url":official}`
    - 防盗提示 → `{"url":"","type":"auto","error":"检测到防盗提示片，当前线路不可播放"}`
    - 其他 → `{"parse":0,"url":url}`
  - `searchContent(key,false)` → `{"limit":n,"list":[...]}`

## 18. LingDu4K（零度4K-迸发）— LingDu4K.java

- HOST：`http://43.248.128.165:9000`
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":{...}}`
  - `categoryContent(...)` → `{"page":pg,"pagecount":99999,"limit":n,"list":[...]}`
  - `detailContent(["id"])` → 标准 vod 结构
  - `playerContent(flag,id,[])` → `{"parse":0,"url":url.url}`
  - `searchContent(key,false)` → `{"limit":n,"list":[...]}`

## 19. ManShanDongMan（漫闪动漫）— ManShanDongMan.java

- HOST：`https://app.manshan.fun`
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":{...}}`
  - `categoryContent(tid,pg,false,{})` → `{"page":pg,"pagecount":99999,"limit":n,"list":[...]}`
  - `detailContent(["id"])` → 标准 vod 结构（`vod_play_from:"漫闪"`）
  - `playerContent(flag,id,[])` → `{"parse":0,"url":res.url,"header":{...}}` 或 `{"parse":0,"url":vid,"header":{...}}`
  - `searchContent(key,false)` → `{"limit":n,"list":[...]}`

## 20. MiFun（MiFun）— MiFun.java

- HOST：`https://getcn.mymifun.com`
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":{...},"list":[...]}`
  - `categoryContent(...)` → `{"page":pg,"pagecount":99999,"limit":n,"list":[...]}`
  - `detailContent(["id"])` → 标准 vod 结构（从 `res.vod` 取字段）
  - `playerContent(flag,id,[])` 多分支：
    - `{"parse":0,"url":j}`
    - `{"parse":0,"url":url}`
    - 兜底 `{"parse":0,"url":id}`
  - `searchContent(key,false)` → `{"limit":n,"list":[...]}`

## 21. MiaoKan4K（秒看4K）— MiaoKan4K.java

- HOST：`https://mk1080.top`（init 时用接口确认）
- 测试用例：
  - `homeContent(true)` → 请求 `{host}/api.php/getappapi.index/initV119` → `{"class":[...],"filters":{...},"list":[...]}`
  - `categoryContent(...)` → 标准分页结构
  - `detailContent(["id"])` → 标准 vod 结构
  - `playerContent(flag,id,[])` → `{"parse":0,"url":...}`
  - `searchContent(key,false)` → `{"limit":n,"list":[...]}`
- 注意：请求头带 `app-api-verify-sign`（时间戳签名）。

## 22. Mino4K（MINO4K）— Mino4K.java

- HOST：`host` 动态（init 时从 `endpoints` 获取）
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":{...},"list":[...]}`
  - `categoryContent(...)` → `{"page":pg,"pagecount":99999,"limit":n,"total":99999,"list":[...]}`
  - `detailContent(["id"])` → 标准 vod 结构
  - `playerContent(flag,id,[])` → `{"parse":0,"url":data.url}`
  - `searchContent(key,false)` → `{"limit":n,"list":[...]}`
- 注意：登录接口获取 token（`/api/auth/login-password`）。

## 23. MuteFun（MuteFun）— MuteFun.java

- HOST：`https://go.5idm.top`
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":{...},"list":[...]}`
  - `categoryContent(...)` → 标准分页结构
  - `detailContent(["id"])` → 标准 vod 结构
  - `playerContent(flag,id,[])` → `{"parse":0,"url":data.play_url}`
  - `searchContent(key,false)` → `{"limit":n,"list":[...]}`

## 24. QingKong（青空次元）— QingKong.java

- HOST：`https://api.sorani.cc`
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":{...},"list":[...]}`
  - `categoryContent(tid,pg,false,{})` → 请求 `{HOST}/sorani-cms/api/video?page=...&categoryId=...` → `{"list":[...]}`（注意该源分页字段可能只有 list）
  - `detailContent(["id"])` → 标准 vod 结构
  - `playerContent(flag,id,[])` → `{"parse":0,"url":data.playUrl}`
  - `searchContent(key,false)` → 请求 `{HOST}/sorani-cms/api/video/search?keyword=...` → `{"list":[...]}`

## 25. SanHaoDongMan（三号动漫）— SanHaoDongMan.java

- HOST：`host` 动态（init 时 AES 解密获取）
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":FILTER_JSON,"list":[...]}`
  - `categoryContent(tid,pg,false,{})` → `{"page":pg,"pagecount":99999,"limit":n,"list":[...]}`
  - `detailContent(["id"])` → 标准 vod 结构
  - `playerContent(flag,id,[])` → `{"parse":0,"url":res.url,"header":{...}}` 或 `{"parse":0,"url":vid,"header":{...}}`
  - `searchContent(key,false)` → `{"limit":n,"list":[...]}`
- 注意：响应体需 AES 解密（`AES2`），请求体需加密（`AES1`）。

## 26. TengXunDanmu（腾讯弹幕）— TengXunDanmu.java

- HOST：`https://pbaccess.video.qq.com`
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":{...},"list":[...]}`
  - `categoryContent(...)` → 标准分页结构
  - `detailContent(["id"])` → 标准 vod 结构
  - `playerContent(flag,id,[])` 多分支：
    - jx7 成功 → `{"parse":0,"url":yulu(解析URL),"danmaku":"本地代理?do=tencent&url=..."}`
    - jx1 成功 → `{"parse":0,"url":changying(解析URL),"danmaku":...}`
    - 兜底 → `{"parse":1,"url":id,"flag":"腾讯","danmaku":...}`
  - `searchContent(key,false)` → `{"limit":n,"list":[...]}`
- 注意：`danmuXml(url)` 静态方法供 `Proxy do=tencent` 调用；解析接口为第三方 HTTP。

## 27. XiFanDongMan（稀饭动漫）— XiFanDongMan.java

- HOST：`https://anime.xifanacg.com`
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":{...},"list":[...]}`
  - `categoryContent(...)` → 标准分页结构
  - `detailContent(["id"])` → 标准 vod 结构
  - `playerContent(flag,id,[])` → `{"parse":0,"url":id}`
  - `searchContent(key,false)` → `{"limit":n,"list":[...]}`

## 28. XiaFan4K（下饭4K）— XiaFan4K.java

- HOST：`http://194.147.100.155:7744`
- 测试用例：
  - `homeContent(true)` → `{"class":[...],"filters":{...},"list":[...]}`
  - `categoryContent(...)` → 标准分页结构
  - `detailContent(["id"])` → 标准 vod 结构
  - `playerContent(flag,id,[])` → `{"parse":0,"url":url}`
  - `searchContent(key,false)` → `{"limit":n,"list":[...]}`

## 29. XingYaDuanJu（星芽短剧）— XingYaDuanJu.java

- HOST：`host` 动态（init 获取）
- 测试用例：
  - `homeContent(true)` → `{"class":[...]}`
  - `homeVideoContent()` → `{"list":[{"vod_id":"id","vod_name":"title","vod_pic":"cover_url","vod_remarks":"play_amount_str"}]}`
  - `categoryContent(...)` → `{"list":[...],"page":pg,"pagecount":9999,"limit":90,"total":999999}`
  - `detailContent(["id"])` → `{"list":[{...,"vod_play_from":线路,"vod_play_url":集数}]}`
  - `playerContent(flag,id,[])` → `{"parse":0,"playUrl":"","url":id,"header":{...}}`
  - `searchContent(key,false)` → `{"list":[...],"page":"1","pagecount":9999,"limit":90,"total":999999}`

## 30. Proxy 代理分支 — Proxy.java

| do | 参数 | 返回 |
| --- | --- | --- |
| `bili` | `bvid,cid,qn,ep_id,season_id` | `200` + `application/dash+xml` + MPD 文本（`BiliCommon.buildMpd`） |
| `tencent` | `url` | `200` + `application/xml` + 弹幕 XML（`TengXunDanmu.danmuXml`） |
| `hanju` | `pid,sq` | `200` + `application/x-mpegurl` + m3u8（`HanXiaoQuan.m3u8`） |
| `ck` | - | `200` + `text/plain` + `ok` |
| `live` | `type=txt,ext` | 直播订阅内容 |
| `MixDemo`/`MixWeb` | `flag,url` | 解析页 HTML |

测试方法：浏览器直接访问 `http://127.0.0.1:9978/proxy?do=ck` 应返回 `ok`；
`.../proxy?do=bili&bvid=BVxxx&cid=xxx` 应返回合法 MPD。

---

## 验收清单（每源必测）

1. `homeContent` 能返回 `class`，App 首页能显示分类。
2. `categoryContent` 翻页正常（page/pagecount/limit）。
3. `detailContent` 能返回 `vod_play_from` + `vod_play_url`（非空）。
4. `playerContent` 返回可播放地址或解析标记（parse 0/1），播放不报"播放失败/地址无效"。
5. `searchContent` 搜索关键词有结果。
6. 需要 `ext` 配置的源（JiuJiuYingShi host、JianPian host、Mino4K/MiFun 登录）确保配置已填。

---

## 修复记录（2026-08-23 按本文件标准检查后）

### 已修复的不合格项

1. **`categoryContent` 的 `page` 字段类型**（28 源）
   - 原实现多为 `put("page", pg)`（字符串），TVBox 要求数值。
   - 已统一改为 `put("page", Integer.parseInt(pg))`；`XingYaDuanJu` 首页固定页改为 `put("page", 1)`。
   - 请求体里的 `page` 参数（POST body）保持不变。

2. **`detailContent` 缺少 `vod_id` / `vod_name` / `vod_pic`**（27 源）
   - 详情页无这三项会导致 App 详情页标题/封面空白。
   - 已按各源 JS 原文件核对真实字段名并补齐（字段来源见下），无对应数据的源填空串。

3. **`HeMaDuanJu` 的 `vod_play_from/vod_play_url` 被 `if (episodes.length() > 0)` 包裹**
   - 无剧集时字段缺失，TVBox 可能判空。已改为三目表达式，字段始终存在。

4. **`MuteFun` / `Lanerc` 播放列表无集名**
   - 原 `vids[j]@@player` / `video[j]@@player` 无集名，列表显示为空。
   - 已改为无 `$` 时自动补 `第n集$vid`，有 `$`（自带集名）则原样保留。

### detailContent 补齐字段来源对照

| 源 | vod_name 来源 | vod_pic 来源 |
| --- | --- | --- |
| AiZhan | `info.vod_name` | `info.vod_pic` |
| BiliHeji | `data.title` | `data.pic`（补 `https:` 前缀） |
| BiliYingshi | `result.title` | `result.cover` |
| CiYuanCheng | `data.title`（原有） | `data.cover_url` |
| DongManGongHeGuo | `data.name` | `data.pic` |
| DuBoKu | `html.Name` | `decode(html.TnId)` |
| DuoDuoZhuiJu | `data[0].vod_name` | `data[0].vod_pic` |
| FengYe4K | 详情页 `h3.slide-info-title` | 详情页 `img[data-src]` |
| GuaZiYingShi | `vodInfo.vod_name`（原有） | `vodInfo.vod_pic` |
| HanXiaoQuan | `series.name` | `series.image.thumb/poster` |
| HeMaDuanJu | `bookInfoVo.title`（原有） | `bookInfoVo.coverWap`（原有） |
| JianPian | `data.title` | `imghost + data.path/thumbnail` |
| JinPaiYingYuan | `data.vodName` | `data.vodPic` |
| JiuJiuYingShi | `data.name` | `data.pic` |
| KuaiYing4K | `vod.vod_name` | `vod.vod_pic` |
| Lanerc | `video_play_info.vod_name` | `video_play_info.vod_pic` |
| LingDu4K | `movieDesc.name` | `movieDesc.cover` |
| ManShanDongMan | `data.title`（原 title 变量） | `data.pic` |
| MiFun | `vod.vod_name` | `vod.vod_pic` |
| MiaoKan4K | `vod.vod_name` | `vod.vod_pic` |
| Mino4K | `data.vod_name` | `data.vod_pic` |
| MuteFun | `html.vod_name` | `html.vod_pic` |
| QingKong | `detail.title` | `detail.cover` |
| SanHaoDongMan | `detail.vod_name` | `detail.vod_pic` |
| TengXunDanmu | `item_params.title` | `item_params.image_url_vertical` |
| XiFanDongMan | 详情页 `h3.slide-info-title` | 详情页 `img[data-src]` |
| XiaFan4K | `data.name` | `data.videoPic` |
| XingYaDuanJu | `data.title` | `data.cover_url` |

### 已通过

- `./gradlew :app:assembleRelease` 编译通过（3 个 JDK 过时警告，无错误）。
- `./jar/genJar.sh` 重新打包成功：`jar/bgcode.jar` md5 `9fd30e50d1dd0b69b8dd5a0b60a3aa9d`，`jar/bgcode.json` 与 `jar/bg/` 副本已同步。

### 待真机/接口实测项（标准验收清单 3/4/5）

- 各源 `playerContent` 实际可播性（部分源需登录/签名/反爬，无法本地全量验证）。
- `searchContent` 关键词结果可用性。
- 建议在 TVBox 中逐源点开详情页确认标题/封面/线路集数显示。

---

## 31. 网络收集新增源（2026-08-23 合并）

> 来源：`github.com/1503304024/CatVodTVSpider`（经典 catvod API，与仓库 `OkHttpUtil/Misc/OKCallBack` 完全兼容）与 `github.com/liuyunfeng001/CatVodTVSpider1`。
> 选择标准：与现有 46 个类去重、站点存活、返回格式符合本文件第 0 节标准。

| 类名 | 站点 | 说明 |
| --- | --- | --- |
| `Aidi` | https://aidi.tv | 爱迪影视，MacCMS 页面解析，多播放源按 `or` 排序 |
| `Auete` | https://auete.com | Auete 影视网 |
| `Cokemv` | https://cokemv.me | COKEMV 影院 |
| `Juhi` | https://www.juhi.cc | 聚核，含内置播放源/筛选配置 |
| `Imaple` | https://imaple.co | 蜜枫，带 Cloudflare 校验，可能需代理 |
| `Bilituys` | https://www.bilituys.com | 哔哩兔影视，`/bilishow/` 分类 |
| `Voflix` | https://www.voflix.me | 域名易变，**必须**在站点 `ext` 填域名 |
| `XingYiYing` | https://www.xingyiying.com | 星影影视 |

### 格式要点

- `Aidi/Auete/Cokemv/Juhi/Imaple`：`categoryContent` 返回 `page`(int)/`pagecount`/`limit`/`total`/`list`；`detailContent` 标准 `vod_*` 全字段 + `vod_play_from/vod_play_url`（多线路 `$$$`、集内 `$`、集间 `#`）。
- `Bilituys/Voflix`：原版缺少 `page` 字段，已补 `"page": Integer.parseInt(pg)`。
- `Voflix.init(Context,String)` 依赖 `ext` 域名，配置里已带 `ext: https://www.voflix.me/`。
- `Juhi/Ysgc` 等经典类 `init` 补了 `throws Exception` 以匹配本仓库 `Spider.init` 签名（原 fork 基类不抛异常）。

### 未收录说明（站点已失效/域名出售）

- `Ysgc`(ik4.cc 域名出售)、`Jumi`、`N0ys`、`Nekk`、`Nfx`、`Buka`、`YydsAli1`、`Enlienli`、`AppYs`、`Ysdq`、`NongMing` 等未并入。
- `DoubanForTVBox` 与现有 `Douban.java` 重复，未并入。
- XPath 系列（`XPathEgg/XPathBde4` 等）依赖已失效的外部解密接口，未并入。

### 打包状态

- `./gradlew :app:assembleRelease` 编译通过。
- `./jar/genJar.sh` 打包：`jar/bgcode.jar` md5 `40a3ade151a175923964a7ad40dc5472`，`jar/bgcode.json` 与 `jar/bg/` 已同步。
- `jar/bgcode.json` sites：41 → 49（新增 8 个）。
