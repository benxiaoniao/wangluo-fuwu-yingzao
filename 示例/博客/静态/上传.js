document.getElementById('图片').addEventListener('change', async function (e) {
  var f = e.target.files[0];
  if (!f) return;
  var box = document.getElementById('图片预览');
  var dataURL = await new Promise(function (res) {
    var r = new FileReader();
    r.onload = function () { res(r.result); };
    r.readAsDataURL(f);
  });
  box.innerHTML = '<p class="弱">上传中…</p>';
  try {
    var resp = await fetch('/上传', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ 名: f.name, 预览: dataURL })
    });
    var j = await resp.json();
    if (j.ok) {
      // 0.3.6 起静态文件直发二进制，优先用服务端回传的文件网址；旧后端回退 dataURL
      var url = j.网址 || j.预览;
      box.innerHTML = '<p class="弱">已保存：<a href="' + url + '">' + j.路径 + '</a></p>' +
        '<img src="' + url + '" style="max-width:360px;border:1px solid #eee;border-radius:8px"/>';
    } else {
      box.innerHTML = '<p class="表单错误">上传失败</p>';
    }
  } catch (err) {
    box.innerHTML = '<p class="表单错误">上传出错：' + err + '</p>';
  }
});