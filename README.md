Akita - бери и тестируй
==========================================

Фреймворк для bdd тестирования на основе cucumber.


В alfaScenario используется хранилище переменных. Для сохранения/изъятия переменных используются методы setVar/getVar. Каждая страница, с которой предполагается взаимодействие, должна быть описана в соответствующем классе, наследующем AlfaPage. Для каждого элемента следует задать имя на русском, через аннотацию @Name, чтобы искать можно было именно по русскому описанию, а не по селектору. Селекторы следует хранить только в классе страницы, не в степах, в степах - взаимодействие по русскому названию элемента.

ru.alfabank.alfatest.cucumber.api.AlfaEnvironment
Класс, связанный с AlfaScenario, используется для хранения страниц и переменных внутри сценария

ru.alfabank.alfatest.cucumber.api.AlfaPage
 Класс-аннотация для реализации паттерна PageObject

ru.alfabank.alfatest.cucumber.api.AlfaScenario
Главный класс, отвечающий за сопровождение тестовых шагов

ru.alfabank.alfatest.cucumber.api.Spectators

ru.alfabank.steps.DefaultApiSteps
Шаги для тестирования API, доступные по умолчанию в каждом новом проекте

ru.alfabank.steps.DefaultSteps
В alfaScenario используется хранилище переменных. Для сохранения/изъятия переменных используются методы setVar/getVar
Каждая страница, с которой предполагается взаимодействие, должна быть описана в соответствующем классе,
наследующем AlfaPage. Для каждого элемента следует задать имя на русском, через аннотацию @Name, чтобы искать
можно было именно по русскому описанию, а не по селектору. Селекторы следует хранить только в классе страницы,
не в степах, в степах - взаимодействие по русскому названию элемента.

ru.alfabank.steps.InitialSetupSteps

ru.alfabank.tests.core.drivers.MobileChrome
Эмуляция мобильной версии браузера Google Chrome

ru.alfabank.tests.core.helpers.PropertyLoader
Класс для получения свойств