# alpha更新日志

## Magisk (2c092ffd-alpha)
- [App] 支持SharedUserId
- [App] 还原boot镜像后删除备份文件
- [App] 内置当前版本更新日志
- [General] 不再自动解锁设备块
- [App] 适配 Android 12L
- [MagiskInit] 重构两步启动的处理方法
- [App] 添加崩溃统计
- [Script] 支持 init_boot 分区
- [App] 允许加载zygisk模块

### 如何安装？
通过Magisk应用来安装和卸载Magisk，一般情况应直接在应用内完成，第一次安装等特殊情况应修补镜像后使用fastboot/odin工具刷入。
通过自定义Recovery不是受支持的方式。

# 上游更新日志

## 2022.3.10 Magisk v24.3

For those coming from v24.1, check the full changelog for changes introduced in v24.2.

- [General] Stop using `getrandom` syscall
- [Zygisk] Update API to v3, adding new fields to `AppSpecializeArgs`
- [App] Improve app repackaging installation workflow

### v24.2

- [MagiskSU] Fix buffer overflow
- [MagiskSU] Fix owner managed multiuser superuser settings
- [MagiskSU] Fix command logging when using `su -c <cmd>`
- [MagiskSU] Prevent su request indefinite blocking
- [MagiskBoot] Support `lz4_legacy` archive with multiple magic
- [MagiskBoot] Fix `lz4_lg` compression
- [DenyList] Allow targeting processes running as system UID
- [Zygisk] Workaround Samsung's "early zygote"
- [Zygisk] Improved Zygisk loading mechanism
- [Zygisk] Fix application UID tracking
- [Zygisk] Fix improper `umask` being set in zygote
- [App] Fix BusyBox execution test
- [App] Improve stub loading mechanism
- [App] Major app upgrade flow improvements
- [General] Improve commandline error handling and messaging
