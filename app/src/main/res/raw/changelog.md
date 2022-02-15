# alpha更新日志

## Magisk (e7c82f20-alpha)
- [App] 支持SharedUserId
- [App] 还原boot镜像后删除备份文件
- [App] 内置当前版本更新日志
- [General] 不再自动解锁设备块
- [App] 适配 Android 12L
- [MagiskInit] 重构两步启动的处理方法
- [App] 添加崩溃统计
- [Script] 支持 init_boot 分区
- [App] 允许加载zygisk模块
- [App] 删除未成功的安装会话文件
- [App] 前台界面跟踪忽略root弹窗界面
- [App] 安装时直接转发文件流
- [App] stub正常更新到完整版时也显示更新完成通知
- [App] 首先尝试root安装

### 如何安装？
通过Magisk应用来安装和卸载Magisk，一般情况应直接在应用内完成，第一次安装等特殊情况应修补镜像后使用fastboot/odin工具刷入。
通过自定义Recovery不是受支持的方式。

# 上游更新日志

## Magisk (1a1b346c) (24102)

## Diffs to v24.1

- [MagiskSU] Fix buffer overflow
- [MagiskSU] Fix owner managed multiuser superuser settings
- [DenyList] Allow targeting processes running as system UID
- [Zygisk] Workaround Samsung's "early zygote"
- [Zygisk] Improved Zygisk loading mechanism
- [App] Fix BusyBox execution test
- [App] Improve stub loading mechanism
- [App] Improve stub upgrade flow
