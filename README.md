<h1> <b> Tugas Besar 1 Strategi Algoritma </b> </h1>

<!-- Penjelasan singkat algoritma greedy yang diimplementasikan
ii. Requirement program dan instalasi tertentu bila ada
iii. Command atau langkah-langkah dalam meng-compile atau build program
iv. Author / identitas pembuat -->

## **Table of Contents**
* [Program Description](#program-description)
* [How to Run Program](#how-to-run-program)
* [Team Members](#team-members)
* [Folders and Files Descriptio](#folders-and-files-description)
* [Extras](#meme-section)

## **Program Description**
<p> Dalam proses pembuatan program dari sebuah bahasa menjadi instruksi yang dapat dieksekusi oleh mesin, terdapat pemeriksaan sintaks bahasa atau parsing yang dibuat oleh programmer untuk memastikan program dapat dieksekusi tanpa menghasilkan error. Parsing ini bertujuan untuk memastikan instruksi yang dibuat oleh programmer mengikuti aturan yang sudah ditentukan oleh bahasa tersebut. Baik bahasa berjenis interpreter maupun compiler, keduanya pasti melakukan pemeriksaan sintaks. Perbedaannya terletak pada apa yang dilakukan setelah proses pemeriksaan (kompilasi/compile) tersebut selesai dilakukan.
 </p>

## **How to Run Program**
1. Clone this repository <br>
`$ git clone https://github.com/NicholasLiem/Tubes1_Algonauts`
2. Change the directory to the location where the main program is stored <br>
`$ cd Tubes1_Algonauts`
3. Make the bot's java file <br>
`$ mvn clean package`
4. Add run java jar command to your run.bat <br>
`$ edit run.bat`
5. 

## **Team Members**
1. Tabitha Permalla - 13521111
2. Nicholas Liem - 13521135
3. Brigita Tri Carolina - 13521156

## **Folders and Files Description**
1. Folder Test <br>
Berisikan segala file .js yang ingin dites oleh program parser.
2. File *main.py* <br>
Berisikan program utama yang menggabungkan segala fungsi dan algoritma yang telah dibuat.
3. File *rules.py* <br>
Berisikan 'aturan' dalam pembacaan file .js menggunakan regex (*regular expression*).
4. File *FA.py* <br>
Berisikan file untuk melakukan pengecekan apakah nama variabel dan penulisan ekspresi telah benar menggunakan FA (*finite automata*).
5. File *grammar_cfg.txt* <br>
Berisikan aturan yang menjadi grammar dasar bagi syntax .js yang akan dites
6. File *grammar_reader.py* <br>
Berisikan algoritma untuk mengubah token yang telah dibaca menggunakan *rules.py* menjadi CNF (*Chomsky Normal Form*) berdasarkan grammar pada file *grammar_cfg.txt*.
7. File *cnf.txt* <br>
Berisikan aturan cnf yang telah dibuat menggunakan algoritma pada file *grammar_reader.py*
8. File *cyk.py* <br>
Berisikan file yang mengandung algoritma untuk membaca CFG (*Context Free Grammar*) berdasarkan CNF yang telah dibuat.
9. Folder *assets* <br>
Berisikan *screenshots* untuk ditampilkan pada README.

## **Meme Section**
![alt text](https://devhumor.com/content/uploads/images/March2017/regex.png)