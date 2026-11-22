# 前端 —— 基石前端（官方 jishi-wasm 运行时）+ 宿主页生成
# 组件用基石写，经官方 jishi_wasm.wasm 在浏览器里运行，用 浏览器 模块操作页面。
# 0.3.6 起构造响应支持字节正文，.wasm 直接以二进制下发（application/wasm）；
# 旧端点 jishi_wasm.wasm.b64（base64 文本通道）保留，给 0.3.5 兼容。
# 顶层名字统一 前端_ 前缀。

导入 文件
导入 路径
导入 编码

# 运行时目录：放 jishi-wasm.js 与 jishi_wasm.wasm（一般是 jishi 安装的 bin 目录）
前端_配置 = {"运行时目录": ""}

函数 前端_设运行时目录(目录):
    前端_配置["运行时目录"] = 目录

# 处理 /网络服务营造/ 下的运行时请求
函数 前端_处理(配置, req):
    令 网址 = req.路径
    如果 网址 == "/网络服务营造/jishi-wasm.js":
        令 js = 文件.读文本(路径.连接(前端_配置["运行时目录"], "jishi-wasm.js"))
        返回 响应(js, 200, "application/javascript; charset=utf-8").输出()
    如果 网址 == "/网络服务营造/jishi_wasm.wasm":
        令 b = 文件.读字节(路径.连接(前端_配置["运行时目录"], "jishi_wasm.wasm"))
        # 字节正文走 构造响应 的字节通道（响应对象.输出 自动分流），字节原样到浏览器
        返回 响应(b, 200, "application/wasm").输出()
    如果 网址 == "/网络服务营造/jishi_wasm.wasm.b64":
        # 旧端点：base64 文本（0.3.5 兼容；引导脚本已改走上面的二进制端点）
        令 b2 = 文件.读字节(路径.连接(前端_配置["运行时目录"], "jishi_wasm.wasm"))
        返回 响应(编码.base64编码(b2), 200, "text/plain; charset=utf-8").输出()
    返回 未找到().输出()

# 生成一个跑基石组件的宿主页
函数 前端_宿主页(标题, 组件源码, 选项 = 空):
    令 导航 = ""
    如果 选项 != 空 与 选项.包含("导航"):
        导航 = 选项["导航"]
    令 主体 = "<p class=\"弱\">前端正在加载基石运行时…</p>"
    如果 选项 != 空 与 选项.包含("主体"):
        主体 = 选项["主体"]
    令 源b64 = 编码.base64编码(编码.文本字节(组件源码))
    令 页 = []
    页.追加("<!DOCTYPE html>")
    页.追加("<html lang=\"zh-CN\"><head><meta charset=\"utf-8\">")
    页.追加("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">")
    页.追加("<title>" + 内建_转义(标题) + "</title>")
    页.追加("<style>" + 前端_样式() + "</style></head><body>")
    页.追加("<header class=\"顶栏\"><h1>" + 内建_转义(标题) + "</h1>" + 导航 + "</header>")
    页.追加("<main><div id=\"组件根\">" + 主体 + "</div></main>")
    页.追加("<script src=\"/网络服务营造/jishi-wasm.js\"></script>")
    页.追加("<script id=\"网造组件源码\" type=\"text/plain\">" + 源b64 + "</script>")
    页.追加("<script>")
    页.追加(前端_引导脚本())
    页.追加("</script>")
    页.追加("</body></html>")
    返回 内建_拼接(页, 字符(10))

# 浏览器侧引导脚本：解码组件源码 + 加载 wasm + 运行
# wasm 直接把网址交给 JishiWasm.load（官方 load 支持字符串网址，自己 fetch），省掉 base64 解码
函数 前端_引导脚本():
    令 行们 = [
        "(async function(){",
        "  try {",
        "    const srcB64 = document.getElementById('网造组件源码').textContent.trim();",
        "    const src = new TextDecoder('utf-8').decode(Uint8Array.from(atob(srcB64), c => c.charCodeAt(0)));",
        "    const jishi = await JishiWasm.load('/网络服务营造/jishi_wasm.wasm');",
        "    window.__网造 = jishi;",
        "    const r = jishi.run(src);",
        "    if (!r.ok) { console.error('基石组件运行出错：', r.error); }",
        "  } catch (e) {",
        "    console.error('前端启动失败：', e);",
        "    const el = document.getElementById('组件根');",
        "    if (el) el.textContent = '前端启动失败：' + e;",
        "  }",
        "})();"
    ]
    返回 内建_拼接(行们, 字符(10))

# 默认样式
函数 前端_样式():
    返回 ":root{--主:#2563eb;--字:#1f2328;--弱:#6b7280;--线:#e5e7eb;--底:#f7f8fa}*{box-sizing:border-box}body{margin:0;font:16px/1.6 system-ui,-apple-system,'Segoe UI','Noto Sans SC',sans-serif;color:var(--字);background:var(--底)}.顶栏{padding:1rem 1.5rem;background:#fff;border-bottom:1px solid var(--线)}.顶栏 h1{margin:0;font-size:1.25rem}main{max-width:52rem;margin:0 auto;padding:1.5rem}.弱{color:var(--弱)}ul.文章列表{list-style:none;padding:0;margin:0}.文章列表 li{padding:.9rem 1rem;background:#fff;border:1px solid var(--线);border-radius:.5rem;margin-bottom:.75rem;display:flex;justify-content:space-between}.文章列表 a{color:var(--主);text-decoration:none}.文章列表 a:hover{text-decoration:underline}.浏览{color:var(--弱);font-size:.875rem}.搜索条{display:flex;gap:.5rem;margin-bottom:1rem}.搜索条 input{flex:1;padding:.5rem .75rem;border:1px solid var(--线);border-radius:.375rem;font:inherit}button{padding:.5rem 1rem;border:0;border-radius:.375rem;background:var(--主);color:#fff;font:inherit;cursor:pointer}"