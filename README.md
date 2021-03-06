UrlShоrtener сервис (JSON API, без UI) создает короткую ссылку по полному URL.

Данный сервис выполняет функции:
1. Создание короткой ссылки по полному URL, короткая ссылка содержит символы из диапазона: [0-9, a-z].
2. По короткой ссылке осуществляется перенаправление браузера пользователя на исходный URL.
3. Для аутентификации пользователя запросы к сервису подписываются цифровой подписью. Формирование подписи производится следующим образом:
a) берутся все параметры запроса в виде ключ=значение, разделителем параметров является символ "&"
б) производится их сортировка в алфавитном порядке по ключу
в) в конец получившейся последовательности отсортированных параметров добавляется секретный ключ
г) итоговая последовательность хэшируется с помощью алгоритма SHA-1
Все условия выполняются лишь при условии, что уникальный идентификатор и секретный ключ заранее известны пользователю.
4. Удаление зарегистрированных коротких ссылок.
5. Установки ограничения на срок использования короткой ссылки.
6. Осуществление подсчета количества переходов по ссылке.
7. Предоставление статистики переходов по ссылкам.
