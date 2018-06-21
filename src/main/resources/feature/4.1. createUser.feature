# language: ru
Функция: Создание нового пользователя

	Сценарий: Успешное отображение страницы входа
		Если я прошел по ссылке 'http://localhost:8080/'
        То откроется страница входа в систему с текстом 'Вход'
        И полями:
        |Пользователь:|
        И кнопками:
        |Ok|
        |Создать|



    Сценарий: Открытие формы для создания нового пользователя
        Когда я нажму кнопку 'Создать'
        То откроется окно с полями:
        |ФИО:   |
        И кнопками:
        |Создать пользователя|
        |Отмена  |

    Сценарий: Создание нового пользователя
        Когда я введу в поле 'ФИО:' 'Александр'
        И нажму кнопку 'Создать пользователя'
        То закроется текущее окно и отобразится страница 'Вход'





