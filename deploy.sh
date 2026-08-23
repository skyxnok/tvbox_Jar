#!/bin/bash
# 一键发布到 Cloudflare Pages（tvbox.201562.xyz）
# 用法：./jar/genJar.sh && ./deploy.sh
set -euo pipefail

STAGE=$(mktemp -d /tmp/pages_deploy.XXXXXX)
mkdir -p "$STAGE/jar/bg"
cp jar/bgcode.json jar/bgcode.jar jar/bgcode_md5.txt "$STAGE/jar/"
cp jar/bg/bgcode.jar jar/bg/bgcode_md5.txt "$STAGE/jar/bg/"
cp pages/index.html "$STAGE/index.html"
cp pages/_redirects "$STAGE/_redirects"

echo "==> 部署到 Cloudflare Pages (tvbox) ..."
npx --yes wrangler pages deploy "$STAGE" --project-name tvbox --branch main

echo
echo "==> 验证线上文件 ..."
echo "spider: $(curl -s https://tvbox.201562.xyz/jar/bgcode.json | python3 -c 'import json,sys;print(json.load(sys.stdin).get("spider",""))')"
echo "live md5: $(curl -s https://tvbox.201562.xyz/jar/bgcode.jar | md5)"
echo "local md5: $(cat jar/bgcode_md5.txt)"
