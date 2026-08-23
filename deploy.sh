#!/bin/bash
# 手动发布到 Cloudflare Pages（tvbox.201562.xyz）
# 说明：仓库已绑定 Pages Git 集成，push 到 main 会自动部署；
# 本脚本用于紧急情况下的手动部署（部署内容 = 当前 git 跟踪的文件）。
set -euo pipefail

if ! git diff --quiet HEAD; then
  echo "!! 工作区有未提交改动，请先 commit，以免部署内容与线上不一致。"
  exit 1
fi

STAGE=$(mktemp -d /tmp/pages_deploy.XXXXXX)
git archive HEAD | tar -x -C "$STAGE"

echo "==> 部署到 Cloudflare Pages (tvbox) ..."
npx --yes wrangler pages deploy "$STAGE" --project-name tvbox --branch main

echo
echo "==> 验证线上文件 ..."
echo "spider: $(curl -s https://tvbox.201562.xyz/jar/bgcode.json | python3 -c 'import json,sys;print(json.load(sys.stdin).get("spider",""))')"
echo "live md5: $(curl -s https://tvbox.201562.xyz/jar/bgcode.jar | md5)"
echo "local md5: $(cat jar/bgcode_md5.txt)"
echo "landing: $(curl -s -o /dev/null -w '%{http_code}' https://tvbox.201562.xyz/)"
