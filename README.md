## Table of contents:
- [About](#about)
  - [DIY Market](#diy_market)
  - [Mission](#mission)
- [Deployment](#deployment)
  - [Database](#database)
  - [Bluemix](#bluemix)
  - [Let's go](#go)

## About

### DIY Market

This Web Service goal is to help a user managing his/her tasks and purchasing products they need (sold by sellers registered in the Web Service). A user has journals corresponding to activities (“Build a Hut”, “Lose Weight”, …) and containing several objectives (“Find the Planks”, “Purchase the Nails”, …), with several posts (“Do I have to take Oak or Birch Planks ?”, “I chose Stainless Steel Nails !”, …). The user can help other users by commenting their posts. He/She can also purchase products he/she needs via the Marketplace and offered by Sellers (from the Web Service himself/herself or from partner Web Services).

### Mission

The mission was to develop a Web Service offering a marketplace and a journal web application. The Web Service had to work with a framework and to be deployed on a web hosting service. These choices were determined by the project team. Both the database model and host were chosen freely by the team. The database and the App Service had to be hosted on two different servers. 
At the beginning of the project, the team defined the Service architecture and the coding languages needed for.
This project is about interoperability. Thus, the Web Service had to be accessible externally by an API but also needed to access APIs from partner Web Services. A user had to be able to log into the service via Facebook or Google.
This Web Service had to be accessible and easy to use on many devices. In fact, the Web Service had to be responsive and the technologies used to design it had to be supported by populal Web Browsers.
To insure the quality of our code, a bunch of test had to be set up.  

## Deployment

### Database

A PostgreSQL database (version 9.4+) is required.
You need to execute these following scripts in order <b>\conf\evolutions\1.sql</b> and <b>\conf\evolutions\2.sql</b>

To register yout database in Play you must modify this file <b>\conf\application.conf</b>

### Bluemix

To deploy on this platform, the database must be hosted and available. In addition, a file <b>\manifest.yml</b> located on the root folder must be sat.

### Let's go

An <b>application secret</b> is required to signing session cookies, CSRF tokens and built in encryption utilities. You need to execute these following commands.

```js
activator
```
and

```js
playGenerateSecret
```
To check this generation see <b>\application.conf</b>.

```js
cf push manifest.yml
```

Your application is hosted, enjoy.