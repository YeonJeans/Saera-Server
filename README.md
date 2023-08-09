![image](https://user-images.githubusercontent.com/61380136/229182068-4f0bd606-8dd3-4e27-a95a-41edc9d37410.png)


<br/>
Saera is providing educational solutions based on the app's high accessibility to help North Koreans make a smooth transition to South Korea.

## ❗️  Introduction
North and South Korea use the same writing system and have similar grammar, but they have developed linguistic differences over the years due to their different environments and societies where they lived for many years after the division. For North Koreans adjusting to life in South Korea, pronunciation and accent can be some of the most challenging differences, leading to discrimination. To bridge this gap, we created SAERA, an app that enables users to learn pronunciation and accent. In pronunciation learning, we provide word examples with standard pronunciation and tips on how to pronounce words and what to pay attention to when pronouncing words based on the differences between South and North Korean. For intonation learning, SAERA presents the target intonation and voice audibly and visually through a Korean TTS and pitch graph generation model. It also visualizes the user's recorded voice in a graph and evaluates it with a rating for each graph. <br/>

With SAERA, we hope North Korean defectors can quickly learn the South Korean language system and overcome language barriers to settle into South Korean society stably.

<br/>

## 🗒  Role division
| Name                     | Role |
|--------------------------|------|
| 김도은 <br/> (Doeun Kim) | - UI Screen <br/>(Splash & Login Screen, Persistent TabBar, Today Learn Screen, Pronunciation Learn Screen, Accent Learn Screen, Create Custom Sentence Screen, MyPage Screen) <br/> - Automatic login <br/> - Today Learn <br/> - Pronunciation Learn <br/> - Accent Learn|
| 남수연 <br/> (Suyeon Nam)|- Extract pitch graph from voice using SPICE<br/>- Similarity Search<br/>- Calculate similarity between two pitch graphs<br/>- Deploy FastAPI + Nginx environment to GCP Virtual Machine<br/>- UX/UI Design<br/>- Train a model to classify end-of-speech pitch (deprecated)|
| 이주은 <br/> (Jueun Lee)          |- Deploy spring server with GCP Virtual Machine<br/>- Manage MySql DB with GCP SQL<br/>- Server APIs|
| 황연진 <br/> (Yeonjin Hwang) | - UI Screen <br/>(Home Screen, Bookmark Screen, Learn Screen, Accent Main Screen, Pronunciation Main Screen, Search Learn Screen, Custom Sentence Loading & Done Screen, Custom Sentence Home Screen, Today Learn Word & Sentence List Screen) <br/> - Home <br/> - Search & Filter <br/> - Learn Pronunciation & Accent Retrieve |

<br/>

## 🛠  Project Architecure

![structure](https://user-images.githubusercontent.com/61380136/228789413-032bc738-8622-4970-944b-4fd2e4136d29.png)

## 📽  Demo Video Link

 [![Sae:ra](https://user-images.githubusercontent.com/61380136/229182496-a3ea7c7f-313d-49b1-8ee0-e5b9ab106999.png)](https://youtu.be/zmuxz9bldj4)
 
 <br/>

## ERD
<img width="771" alt="image" src="https://github.com/YeonJeans/Saera-Server/assets/68546023/9417e300-dec9-4474-9340-779279782fd9">
<img width="446" alt="image" src="https://github.com/YeonJeans/Saera-Server/assets/68546023/3f24a78b-c1e2-43e1-95f6-3a968ff97b32">

<br/>

## API
![image](https://github.com/YeonJeans/Saera-Server/assets/68546023/2265013e-9f1c-4354-974d-a1f5ae7bf9e7)



