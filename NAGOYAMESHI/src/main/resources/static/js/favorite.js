document.addEventListener("DOMContentLoaded", () => {
  const deleteBtn = document.getElementById("favorite-delete-btn");
  const addForm = document.getElementById("favorite-add-form");

  if (deleteBtn) {
    deleteBtn.addEventListener("click", function (e) {
      e.preventDefault(); 

      const restaurantId = deleteBtn.dataset.restaurantId;
      const favoriteId = deleteBtn.dataset.favoriteId;

      const csrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
      const csrfHeader = document.querySelector("meta[name='_csrf_header']").getAttribute("content");

      fetch(`/favorites/ajax/delete/${restaurantId}/${favoriteId}`, {
        method: "POST",
        headers: {
          [csrfHeader]: csrfToken
        }
      })
      .then(response => {
        if (!response.ok) {
          throw new Error("HTTPステータス: " + response.status);
        }
        return response.text();
      })
      .then(result => {
        if (result === "OK") {
          deleteBtn.style.display = "none";
          if (addForm) {
            addForm.style.display = "block";
          } else {
            console.warn("お気に入り追加フォームが見つかりませんでした");
          }
        } else {
          alert("お気に入り解除に失敗しました: " + result);
        }
      })
      .catch(error => {
        console.error("通信エラー:", error);
        alert("通信エラーです: " + error.message);
      });
    });
  }
});
