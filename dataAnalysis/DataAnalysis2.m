close all;
data = csvread('outFile2.txt');
contourf(1:160,1:121,data.','LineColor','none')
