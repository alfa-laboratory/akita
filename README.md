<img align="left" width="140" height="140" title="akita"
     src="https://github.com/alfa-laboratory/akita/blob/master/akita.png" />

# Akita
[![Build Status](https://travis-ci.org/alfa-laboratory/akita.svg?branch=master)](https://travis-ci.org/alfa-laboratory/akita)
[![Coverage Status](https://coveralls.io/repos/github/alfa-laboratory/akita/badge.svg?branch=master)](https://coveralls.io/github/alfa-laboratory/akita?branch=master)
[![Download](https://api.bintray.com/packages/alfa-laboratory/maven-releases/akita/images/download.svg) ](https://bintray.com/alfa-laboratory/maven-releases/akita/_latestVersion)

Akita - бери и тестируй
=========================

BDD библиотека шагов для тестирования на основе cucumber и selenide.
Тесты пишутся на русском языке и представляют собой пользовательские сценарии, которые могут выступать в качестве пользовательской документации на приложение.

Для написания тестового сценария достаточно подключить библиотеку и воспользоваться любым готовым шагом из ru.alfabank.steps

Например:
```
Функционал: Страница депозитов
  Сценарий: Открытие депозита
    Допустим совершен переход на страницу "Депозиты" по ссылке из property файла = "depositsUrl"
    Когда выполнено нажатие на кнопку "Открыть депозит"
    Тогда страница "Открытие депозита" загрузилась
```


application.properties
=======================
Для указания дополнительных параметров или тестовых данных создайте в своем проекте файл application.properties
в main/java/resources

Работа со страницами
====================
Для работы с элементами страницы ее необходимо задать как текущую.
Таким образом можно получить доступ к методам взаимодействия с элементами, описанным в AkitaPage.

Новую текущую страницу можно установить шагом
```Когда страница "<Имя страницы>" загрузилась```

Для страницы депозитов шаг может выглядеть так
```Когда страница "Депозиты" загрузилась```

Каждая страница, с которой предполагается взаимодействие, должна быть описана в классе наследующемся от AkitaPage.
Для страницы и ее элементов следует задать имя на русском, через аннотацию Name, чтобы искать можно было именно по русскому описанию.
Элементы страницы ищутся по локаторам, указанным в аннотации FindBy и должны иметь тип SelenideElement или List<SelenideElement>.

Пример описания страницы:
```java
    @Name("Депозиты")
    public class DepositsPage extends AkitaPage {

        @FindBy(css = ".deposit_open")
        @Name("Открыть депозит")
        private SelenideElement depositOpenButton;

        @FindBy(css = ".deposit_close")
        @Name("Закрыть депозит")
        private SelenideElement depositCloseButton;

        @FindBy(css = ".deposit_list")
        @Name("Список депозитов")
        private List<SelenideElement> depositList;
    }
```

Инициализация страницы
Страница инициализируется каждый раз, когда вызываются методы initialize(<Имя класса страницы>.class)

Пример инициализации страницы "Депозиты":
```
DepositsPage page = (DepositsPage) getCurrentPage();
akitaaScenario.setCurrentPage(page.initialize().appeared());
```

Пример получения конкретной страницы:
```
DepositsPage page = akitaScenario.getPage(DepositsPage.class);
```

Другой способ работы с методами страницы - это использование AkitaScenario.withPage
Пример использования: ```withPage(TestPage.class, page -> { some actions with TestPage methods});```

Для страницы инициализируется карта ее элементов - это те поля, что помечены аннотацией Name.
Кроме того, осуществляется проверка, что загружена требуемая страница.
Страница считается загруженной корректно, если за отведенное по умолчанию время были загружены основные ее элементы.
Основными элементами являются поля класса страницы с аннотацией Name, но без аннотации Optional.
Аннотация Optional указывает на то, что элемент является не обязательным для принятия решения о загрузке страницы.
Например, если на странице есть список, который раскрывается после нажатия не него, т.е. видим не сразу после загрузки страницы,
его можно пометить как Optional.
Реализована возможность управления временем ожидания появления элемента на странице.
Чтобы установить timeout, отличный от базового, нужно добавить в application.properties строку
waitingAppearTimeout=150000

Доступ к элементам страницы
============================
Данные строки позволяют по имени элемента найти его в карте элементов текущей страницы.

```
akitaScenario.getCurrentPage().getElement("Открыть депозит")
akitaScenario.getCurrentPage().getElementsList("Список депозитов")
 ```


Работа с REST запросами
=======================

В библиотеке реализована возможность отправки REST запросов и сохранения ответа в переменную.

Поддерживаются следующие типы запросов: GET, POST.
   ```Когда выполнен POST запрос на URL "{depositsApi}deposits/{docNumber}/repay" с headers и parameters из таблицы. Полученный ответ сохранен в переменную
       | type   | name          | value           |
       | header | applicationId | test            |
       | header | customerId    | <userCus>       |
       | body   | repayment     | <fileForCreate> |
  ```
В таблице переменных поддерживаются типы: header, parameter, body
Для body-параметра сейчас поддерживается как работа с телом запроса, хранящимся в папке restBodies, так и с указанием текста body в самом шаге в соответствующей ячейке
Значения параметров таблицы и частей url можно указывать в application.properties

Отображение в отчете справочной информации
============================================

Для того, чтобы в отчете появился блок Output с информацией, полезной для анализа отчета, можно воспользоваться следующим методом
 ```
akitaScenario.write("Текущий URL = " + currentUrl + " \nОжидаемый URL = " + expectedUrl);
 ```

Проверка логического выражения
===============================
У нас есть шаг, который например может выглядеть так:
 ```
Тогда верно что "amountToPay == amountMonthly + penalty + 100"
 ```
Важно отметить, что равенство проверяется использованием операнда "==", неравенство, как "!="

Использование переменных
=========================
Иногда есть необходимость использовать значения из одного шага в последующих.
Для этого реализовано хранилище переменных в AkitaScenario.
Для сохранения/изъятия переменных используются методы setVar/getVar.

Сохранение переменной в хранилище:
```akitaScenario.setVar(<имя переменной>, <значение переменной>);```

Получение значения переменной из хранилища:
```akitaScenario.getVar(<имя переменной>)```

Краткое описание главных классов
=================================

```ru.alfabank.alfatest.cucumber.api.AkitaEnvironment```
Используется для хранения страниц и переменных внутри сценария
scenario - Сценарий из Cucumber.api, с которым связана среда

```ru.alfabank.alfatest.cucumber.api.AkitaPage```
Класс для реализации паттерна PageObject. Тут описаны основные методы взаимодействия с элементами страницы

```ru.alfabank.alfatest.cucumber.api.AkitaScenario```
Позволяет заполнить хранилище переменных, существующее в рамках одного сценария, значениями и читать эти значения при необходимости.

```ru.alfabank.steps.DefaultApiSteps```
Шаги для тестирования API, доступные по умолчанию в каждом новом проекте

```ru.alfabank.steps.DefaultSteps```
Шаги для тестирования UI, доступные по умолчанию в каждом новом проекте

```ru.alfabank.steps.InitialSetupSteps```
Хуки предустановок, где происходит создание, закрытие браузера, получение скриншотов

```ru.alfabank.tests.core.drivers.MobileChrome```
Класс, описывающий создание экземпляра мобильной версии Google Chrome драйвера

```ru.alfabank.tests.core.helpers.PropertyLoader```
Класс для получения свойств

# Template
https://github.com/alfa-laboratory/akita-testing-template

Используемые зависимости:
--------------------------
> nebula-release-plugin - Apache License Version 2.0
> coveralls-gradle-plugin - The MIT License (MIT)
> com.codeborne.selenide - The MIT License (MIT)
> io.rest-assured.rest-assured - Apache License Version 2.0
> com.google.inject.guice - Apache License Version 2.0
> org.mockito.mockito-core - The MIT License
> com.github.tomakehurst:wiremock - Apache License Version 2.0
>org.hamcrest.hamcrest-all - BSD License
>org.codehaus.groovy - Apache License Version 2.0
>JUnit - Eclipse Public License
>org.slf4j.slf4j-simple - The MIT License (MIT)
>org.projectlombok.lombok - The MIT License (MIT)
>info.cukes.cucumber-java - The MIT License (MIT)
>info.cukes.cucumber-core - The MIT License (MIT)
>org.reflections.reflections
