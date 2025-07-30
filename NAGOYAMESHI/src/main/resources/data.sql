INSERT IGNORE INTO roles (id, name) VALUES 
(1, 'ADMIN'),
(2, 'FREE'),
(3, 'PREMIUM');

INSERT IGNORE INTO categories (id, name) VALUES
(1, '和食'),
(2, 'イタリアン'),
(3, '中華'),
(4, '韓国料理'),
(5, 'フレンチ'),
(6, 'カフェ'),
(7, 'バー'),
(8, '焼肉'),
(9, 'ラーメン'),
(10, 'スイーツ'),
(11, 'うどん'),
(12, 'そば'),
(13, 'しゃぶしゃぶ'),
(14, 'すき焼き'),
(15, '寿司'),
(16, '天ぷら'),
(17, 'お好み焼き'),
(18, 'たこ焼き'),
(19, '丼物'),
(20, '鍋料理'),
(21, 'カレー'),
(22, '鉄板焼き'),
(23, 'ステーキ'),
(24, 'ハンバーグ'),
(25, 'パン'),
(26, '定食'),
(27, '海鮮料理'),
(28, '喫茶店'),
(29, 'スペイン料理'),
(30, 'タイ料理'),
(31, 'ハンバーガー'),
(32, '揚げ物'),
(33, '洋食'),
(34, 'エスニック'),
(35, 'ベジタリアン'),
(36, '創作料理');

INSERT IGNORE INTO users (
  id, name, email, phone_number, password,
  role_id, is_premium, stripe_subscription_id, stripe_customer_id
) VALUES
(1, '一般 太郎',       'user@example.com',      '09012345678', 'password', 2, false, NULL, NULL),
(2, '有料 花子',       'premium@example.com',   '09023456789', 'password', 3, true, 'sub_abc001', 'cus_abc001'),
(3, '管理者 次郎',     'admin@example.com',     '09034567890', 'password', 1, false, NULL, NULL),
(4, '有料 次郎',       'premium2@example.com',  '09045678901', 'password', 3, true, 'sub_abc002', 'cus_abc002'),
(5, '有料 三郎',       'premium3@example.com',  '09056789012', 'password', 3, true, 'sub_abc003', 'cus_abc003'),
(6, '有料 四郎',       'premium4@example.com',  '09067890123', 'password', 3, true, 'sub_abc004', 'cus_abc004'),
(7, '有料 五郎',       'premium5@example.com',  '09078901234', 'password', 3, true, 'sub_abc005', 'cus_abc005'),
(8, '有料 六郎',       'premium6@example.com',  '09089012345', 'password', 3, true, 'sub_abc006', 'cus_abc006'),
(9, '有料 七子',       'premium7@example.com',  '09090123456', 'password', 3, true, 'sub_abc007', 'cus_abc007'),
(10,'有料 八子',       'premium8@example.com',  '09001234567', 'password', 3, true, 'sub_abc008', 'cus_abc008');

INSERT IGNORE INTO restaurants (
  id, category_id, name, image_name, menu_image_name, description,
  lunch_price_min, lunch_price_max, dinner_price_min, dinner_price_max,
  postal_code, address, phone_number, holiday,
  lunch_start, lunch_end, dinner_start, dinner_end,
  created_at, updated_at
) VALUES
(1, 1, '居酒屋名古屋', 'nagoya_izakaya.jpg', 'izakaya_menu1.jpg', '活気のある名古屋の居酒屋',
 1000, 2000, 3000, 4000, '460-0008', '名古屋市中区栄1-1-1', '052-000-1111', '水曜日',
 '11:00:00', '14:00:00', '17:00:00', '22:00:00', NOW(), NOW()),

(2, 2, 'NAGOYACAFE', 'nagoya_cafe.jpg', 'cafe_menu1.jpg', 'おしゃれなイタリアンCAFEで窯焼きのピザをどうぞ',
 NULL, NULL, 4000, 5000, '460-0010', '名古屋市中村区名駅2-2-2', '052-000-2222', '火曜日',
 NULL, NULL, '17:00:00', '23:00:00', NOW(), NOW()),

(3, 3, '中華居酒屋 龍門', 'chcuka.jpg', 'chuka_menu.jpg', '本格中華料理と紹興酒が楽しめるお店',
 900, 1500, 2000, 3500, '460-0011', '名古屋市中区錦3-10-15', '052-111-2222', '月曜日',
 '11:30:00', '14:30:00', '17:30:00', '22:30:00', NOW(), NOW()),

(4, 5, 'ビストロラパン', 'italian.jpg', 'hurenchi_menu.jpg', '名古屋駅近くのカジュアルフレンチ',
 1200, 2000, 4000, 6000, '450-0002', '名古屋市中村区名駅3-3-3', '052-333-4444', '火曜日',
 '11:00:00', '15:00:00', '18:00:00', '22:00:00', NOW(), NOW()),

(5, 9, 'ラーメン 龍神', 'nagoya_ramen.jpg', 'ramen_menyu1.jpg', '魚介とんこつの濃厚スープが人気のラーメン店',
 NULL, NULL, 1000, 1200, '461-0001', '名古屋市東区東桜2-1-1', '052-555-6666', '年中無休',
 NULL, NULL, '18:00:00', '02:00:00', NOW(), NOW()),

(6, 10, 'スイーツ工房 HappyBerry', 'happyberry.jpg', 'happyberry_menu.jpg', '季節のフルーツを使ったスイーツ専門店',
 800, 1500, NULL, NULL, '460-0012', '名古屋市中区大須2-2-2', '052-777-8888', '水曜日',
 '10:00:00', '18:00:00', NULL, NULL, NOW(), NOW()),

(7, 6, 'カフェモカ 名古屋本店', 'nagoya_cafe.jpg', 'cafe_menu1.jpg', '落ち着いた雰囲気でコーヒーと自家製ケーキを提供',
 NULL, NULL, 1200, 2500, '460-0014', '名古屋市中区栄2-4-12', '052-111-9999', '木曜日',
 NULL, NULL, '11:00:00', '22:00:00', NOW(), NOW()),

(8, 8, '焼肉一番星', 'yakiniku.jpg', 'yakiniku_menu.jpg', '黒毛和牛をリーズナブルに味わえる焼肉屋',
 1500, 2500, 5000, 8000, '460-0015', '名古屋市中区千代田1-5-5', '052-222-3333', '火曜日',
 '12:00:00', '15:00:00', '17:00:00', '23:00:00', NOW(), NOW()),

(9, 20, '名古屋鍋処 ゆらり', 'nagoya_izakaya.jpg', 'nabe_menu.jpeg', '季節の食材を使った創作鍋料理のお店',
 NULL, NULL, 3500, 5000, '460-0020', '名古屋市昭和区広路町1-1-1', '052-444-5555', '水曜日',
 NULL, NULL, '18:00:00', '23:30:00', NOW(), NOW()),

(10, 21, 'カレーキッチン SpiceBox', 'kare_tenpo.jpg', 'kare_menu.jpg', 'スパイス香る創作カレー専門店',
 900, 1200, NULL, NULL, '460-0021', '名古屋市西区城西3-3-3', '052-666-7777', '月曜日',
 '11:30:00', '15:00:00', NULL, NULL, NOW(), NOW()),

(11, 15, '寿司匠', 'sushi.jpg', 'sushi_menu1.jpg', '職人技が光る江戸前寿司を提供',
 NULL, NULL, 7000, 10000, '460-0022', '名古屋市中村区鳥居通1-2-3', '052-888-9999', '日曜日',
 NULL, NULL, '17:00:00', '22:00:00', NOW(), NOW()),

(12, 34, 'エスニック酒場 バルサ', 'chcuka.jpg', 'top_image3.jpg', 'ベトナム・タイ・インドネシア料理が味わえる多国籍バル',
 1000, 1500, 3000, 4500, '460-0023', '名古屋市中区新栄町5-5-5', '052-101-2020', '木曜日',
 '11:00:00', '14:00:00', '18:00:00', '00:00:00', NOW(), NOW());

INSERT IGNORE INTO reviews (restaurant_id, user_id, score, content) VALUES
(1, 2, 5, '料理も雰囲気も最高でした！また行きたいです。'),
(1, 3, 4, '刺身の鮮度が素晴らしくて、日本酒と一緒に楽しめました！'),
(1, 4, 3, '少し騒がしかったけど、料理は平均以上。もう少し静かなら高評価でした。'),
(1, 5, 4, 'ランチの定食がボリューム満点でコスパ良かったです。'),
(1, 6, 5, '名古屋名物の手羽先が最高！外はパリッと中はジューシー。'),
(1, 7, 4, '席が広めでゆっくりできました。家族連れでも安心な感じ。'),
(1, 8, 5, 'スタッフが親切で、楽しい夜を過ごせました。'); 