<h1> <b> Galaxio Bot - Algonauts </b> </h1>

## **Table of Contents**
* [Program Description](#program-description)
* [System Requirements](#system-requirements)
* [How to Run Program](#how-to-run-program)
* [Team Members](#team-members)
* [Folders and Files Descriptio](#folders-and-files-description)
* [Extras](#meme-section)

## **Program Description**
<p> Galaxio adalah sebuah game battle royale yang mempertandingkan bot kapal anda
dengan beberapa bot kapal yang lain. Setiap pemain akan memiliki sebuah bot kapal dan tujuan dari permainan adalah agar bot kapal anda yang tetap hidup hingga akhir permainan. Penjelasan lebih lanjut mengenai aturan permainan akan dijelaskan di bawah. Agar dapat memenangkan pertandingan, setiap bot harus mengimplementasikan strategi tertentu untuk dapat memenangkan permainan.
 </p>
 <p> Program ini adalah bot yang didesain sedemikian rupa untuk
 menjadi bot yang dapat memenangkan game Galaxio. Algoritma utama yang
 digunakan dalam membuat bot ini adalah strategi algoritma Greedy. </p>

<p> 
Kami menyebut algoritma yang kami terapkan ini sebagai algoritma greedy by survival. Tujuan akhir yang ingin dicapai dari algoritma ini adalah menjadi bot yang
bertahan sampai akhir permainan.
Dari sisi greedy by points dan juga greedy by attack, bot akan memprioritaskan
untuk mengonsumsi musuh. Ketika bot sudah menetapkan musuh sebagai target,
bot akan mengejar dan menembaki musuh, dan kemudian mengonsumsi musuh.
Prioritas kedua, khususnya apabila tidak terdapat musuh yang lebih kecil daripada
bot, adalah mengonsumsi makanan sebanyak-banyaknya.
Dari sisi greedy by points dan juga greedy by defense, bot akan memperhitungkan defense yang paling menguntungkan. Apabila memungkinkan untuk memasuki wormhole, bot akan bergerak menuju wormhole terdekat. Selain menjadi sarana
untuk menghindar, melewati wormhole juga menambah poin. Apabila sulit untuk
menghindar, maka bot akan mengaktifkan shield untuk meminimalkan damage.
Secara umum, algoritma yang kami terapkan bertujuan untuk mempertahankan
bot selama mungkin dalam permainan. Tujuan ini diharapkan dapat tercapai dengan jalan mengalahkan musuh sembari melindungi diri.

Dalam perancangan bot kami, alur berpikir kami adalah sebagai berikut:
1. Prioritaskan menyerang dan mengonsumsi musuh
2. Apabila tidak ada musuh yang lebih kecil, prioritaskan mengonsumsi makanan
3. Apabila musuh yang lebih besar mengejar bot, prioritaskan kabur sembari
mencari makanan; apabila tidak ada makanan dalam jarak dekat, dan posisi
bot lebih dekat ke wormhole, maka bergerak menuju wormhole
4. Apabila terdapat torpedo yang mendekat, prioritaskan menggunakan shield
5. Dalam semua situasi, sebisa mungkin menghindari border, gas cloud, asteroid
serta objek-objek lain yang mengancam
6. Selain dalam situasi kabur dari musuh, bot akan menghindari wormhole (untuk
menghindari perpindahan yang tidak peru)
</p>

## **System Requirements**
1. Java (ver.11 Minimum):
https://www.oracle.com/id/java/
2. NodeJs: https://nodejs.org/en/download/
3. .Net Core 3.1:
https://dotnet.microsoft.com/en-us/download/dotnet/3.1
4. .Net Core 5:
https://dotnet.microsoft.com/en-us/download/dotnet/5.0

## **How to Run Program**
1. Clone this repository <br>
`$ git clone https://github.com/NicholasLiem/Tubes1_Algonauts`
2. Change the directory to the location where the main program is stored <br>
`$ cd Tubes1_Algonauts`
3. Make the bot's java file <br>
`$ mvn clean package`
4. Add run java jar command to your run.bat <br>
a. Open your run.bat and add command <br> 
`start "" java -jar Algonauts.jar` <br>
b. If you want to start it without run.bat, type <br>
`java -jar Algonauts.jar` in the command line

## **Team Members**
1. Tabitha Permalla - 13521111
2. Nicholas Liem - 13521135
3. Brigita Tri Carolina - 13521156

## **Folders and Files Description**
1. Folder doc <br>
Berisikan file laporan dari tugas besar ini<br>
2. Folder src <br>
2a. Enums <br>
Berisi enum atau dictionary tentang ObjectTypes dan PlayerActions <br>
2b. Models <br>
Terdiri dari definisi kelas GameObject, GameState, GameStateDto, PlayerAction, Position, dan World. <br>
2c. Services <br>
Terdiri dari BotService.java yang merupakan inti utama dari bot ini.
3. Folder target <br>
3a. Berisikan file .jar bot

## **Meme Section**
![alt text](https://www.thecoderpedia.com/wp-content/uploads/2020/06/Programming-Memes-Java-Jokes.jpg)