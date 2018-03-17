close all;

for indexBig = 1:3
    clearvars -except indexBig;
    data = csvread(['outFile' num2str(indexBig) '.txt']);
    parents = data(:,1:2);
    children = data(:,3:4);
    time_move = data(:,6);
    time_stay_parent = data(:,5);
    
    subplot(2,2,indexBig);hold on;
    grey = [0.6 0.6 0.6];
    scaleFactorEdge = 20;
    plotEdges;
    scaleFactorNode = 10;
    plotNodes;

    xlim([15 145])
    ylim(ylim+[-5 5]);
end