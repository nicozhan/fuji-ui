#!/usr/bin/env bash
# Fuji UI 版本回滚脚本
# 用法：
#   ./rollback.sh list                查看所有可回滚的版本
#   ./rollback.sh v1.1.0              回滚到 v1.1.0（回滚前自动打 backup 标签）
#   ./rollback.sh <commit>            回滚到任意提交（如 2b3a0ea）
#   ./rollback.sh --preview v1.0.0    只把该版本的文件导出到 .rollback-preview/ 查看，不改动当前代码
set -euo pipefail
cd "$(dirname "$0")"

bold() { printf '\033[1m%s\033[0m\n' "$1"; }
die()  { printf '\033[31m错误：%s\033[0m\n' "$1" >&2; exit 1; }

list_versions() {
  bold "== 可回滚版本（标签）=="
  if [ -z "$(git tag -l)" ]; then
    echo "（还没有标签，先执行 ./rollback.sh --tag 记录当前状态）"
  else
    git tag -l --sort=-creatordate --format='%(refname:short)  %(creatordate:format:%Y-%m-%d %H:%M)  %(contents:subject)'
  fi
  echo
  bold "== 全部提交（可作为回滚点）=="
  git log --pretty=format:'%h  %ad  %s' --date=format:'%Y-%m-%d %H:%M'
  echo
}

preview() {
  local target="$1"
  git rev-parse --verify "$target" >/dev/null 2>&1 || die "找不到版本：$target"
  rm -rf .rollback-preview
  mkdir -p .rollback-preview
  git archive "$target" | tar -x -C .rollback-preview
  bold "已导出 $target 到 .rollback-preview/ （当前代码未改动）"
  echo "可直接打开 .rollback-preview/index.html 对比预览。"
}

do_rollback() {
  local target="$1"
  git rev-parse --verify "$target" >/dev/null 2>&1 || die "找不到版本：$target"
  [ "$(git rev-parse "$target^{commit}")" = "$(git rev-parse HEAD)" ] && die "当前已经在该版本上"

  local dirty
  dirty=$(git status --porcelain)
  if [ -n "$dirty" ]; then
    bold "检测到未提交的改动，先自动暂存（git stash），回滚后可 git stash pop 找回："
    echo "$dirty"
    git stash push -u -m "rollback-auto-stash $(date +%Y%m%d-%H%M%S)" >/dev/null
  fi

  local backup="backup-$(date +%Y%m%d-%H%M%S)"
  git tag -a "$backup" -m "回滚前自动备份（from $(git rev-parse --short HEAD)）"
  bold "已把回滚前状态备份为标签：$backup"

  git reset --hard "$target"
  bold "已回滚到 $target（$(git log -1 --pretty=%s)）"
  echo "如需撤销本次回滚：./rollback.sh $backup"
}

case "${1:-}" in
  ""|-h|--help)
    sed -n '2,8p' "$0" | sed 's/^# \{0,1\}//'
    ;;
  list|-l)
    list_versions
    ;;
  --preview)
    [ -n "${2:-}" ] || die "用法：./rollback.sh --preview <版本>"
    preview "$2"
    ;;
  --tag)
    name="${2:-v$(date +%Y%m%d-%H%M)}"
    git tag -a "$name" -m "手动存档 $name"
    bold "已创建标签：$name"
    ;;
  *)
    do_rollback "$1"
    ;;
esac
