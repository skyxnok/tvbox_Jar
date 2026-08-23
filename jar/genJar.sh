#!/bin/bash
###
 # @Author: catvod
 # @Date: 2025-03-10 12:38:35
 # @LastEditTime: 2025-03-11 17:59:29
 # @LastEditors: bgcode
 # @Description: 描述
 # @FilePath: /TVjar/jar/genJar.sh
 # 本项目采用GPL 许可证，欢迎任何人使用、修改和分发。
### 

# 删除旧的 JAR 文件
rm -f ./jar/bgcode.jar

# 删除旧的 Smali 类目录
rm -rf ./jar/Smali_classes

# 反编译 DEX 文件为 Smali 代码
java -jar ./jar/3rd/baksmali-2.5.2.jar d ./app/build/intermediates/dex/release/minifyReleaseWithR8/classes.dex -o ./jar/Smali_classes

# 删除旧的 spider 和 parser 目录
rm -rf ./jar/spider.jar/smali/com/github/catvod/spider
rm -rf ./jar/spider.jar/smali/com/github/catvod/parser

# 创建必要的目录
mkdir -p ./jar/spider.jar/smali/com/github/catvod/

# 执行加密操作（如果参数为 ec）
if [ "$1" = "ec" ]; then
    java -Dfile.encoding=utf-8 -jar ./jar/3rd/oss.jar ./jar/Smali_classes
fi

# 移动 spider 和 parser 目录
mv ./jar/Smali_classes/com/github/catvod/spider ./jar/spider.jar/smali/com/github/catvod/
mv ./jar/Smali_classes/com/github/catvod/parser ./jar/spider.jar/smali/com/github/catvod/

# 删除临时的 Smali 类目录
rm -rf ./jar/Smali_classes

# 重新打包为 APK
java -jar ./jar/3rd/apktool_2.4.1.jar b ./jar/spider.jar -c
ls 
# 移动生成的 DEX JAR 文件
mv ./jar/spider.jar/dist/dex.jar ./jar/bgcode.jar

# 计算 MD5 哈希值
md5sum ./jar/bgcode.jar | awk '{print $1}' > ./jar/bgcode_md5.txt

# 删除临时目录
rm -rf ./jar/spider.jar/smali/com/github/catvod/spider
rm -rf ./jar/spider.jar/smali/com/github/catvod/parser
rm -rf ./jar/spider.jar/build
rm -rf ./jar/spider.jar/dist

new_md5=$(cat ./jar/bgcode_md5.txt)

# 替换a.json文件中的MD5码
# 安全地读取文件内容并转义特殊字符
new_md5=$(cat ./jar/bgcode_md5.txt | sed 's/[&/]/\\&/g')

old_str=";md5;[^,]*"
new_str=";md5;$new_md5\""

# 使用 sed 进行替换，并创建备份文件
sed -i.bak "s/$old_str/$new_str/g" ./jar/bgcode.json 

mkdir -p ./jar/bg
cp ./jar/bgcode.jar ./jar/bg/bgcode.jar
cp ./jar/bgcode_md5.txt ./jar/bg/bgcode_md5.txt