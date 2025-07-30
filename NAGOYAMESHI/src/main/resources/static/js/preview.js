function previewImage(event, previewId) {
  const file = event.target.files[0];
  const previewContainer = document.getElementById(previewId);
  if (file && file.type.startsWith("image/")) {
    const reader = new FileReader();
    reader.onload = function(e) {
      previewContainer.innerHTML =
        `<img src="${e.target.result}" class="img-fluid border" style="max-height:300px;">`;
    };
    reader.readAsDataURL(file);
  } else {
    previewContainer.innerHTML =
      "<p class='text-danger'>画像ファイルを選択してください</p>";
  }
}
