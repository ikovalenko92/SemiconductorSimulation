close all;clear all;

st.data1 = [];
st.data2 = [];
st.data3 = [];
results.data1 = [];
results.data2 = [];
results.data3 = [];

%Clean data
for testIndex = 1:5
    st.data1 = [st.data1; csvread(['T' num2str(testIndex) 'outFile' num2str(1) '.txt'])];
    st.data2 = [st.data2; csvread(['T' num2str(testIndex) 'outFile' num2str(2) '.txt'])];
    st.data3 = [st.data3; csvread(['T' num2str(testIndex) 'outFile' num2str(3) '.txt'])];
    
    results1 = readtable(['T' num2str(testIndex) 'outFile50000.0.txt']);
    results2 = readtable(['T' num2str(testIndex) 'outFile100000.0.txt']);
    results3 = readtable(['T' num2str(testIndex) 'outFile150000.0.txt']);

    results.data1 = [results.data1; results1.Var5'];
    results.data3 = [results.data3; results3.Var5'];
    
    if(size(results2.Var5,1)==4)
       results.data2 = [results.data2; [results2.Var5(1:end-1)' 0 results2.Var5(end)' 0]];
    elseif (results2.Var5(4,1) >25)
       results.data2 = [results.data2; [results2.Var5(1:end-2)' 0 results2.Var5(end-1:end)']];
    else
       results.data2 = [results.data2; results2.Var5'];
    end
end

figureProps.maxNodes = zeros(1,3);
figureProps.maxEdges = zeros(1,3);

for indexBig = 1:3
    data = st.(['data' num2str(indexBig)]);
    data(data<0) = 0;
    parents = data(:,1:2);
    children = data(:,3:4);
    time_move = data(:,6);
    
    %time_stay_parent = data(:,5)
    
    time_wait_robot = zeros(size(data(:,5)));
    time_wait_machine = zeros(size(data(:,5)));
    
    for ind = 1:size(data,1)
        if(children(ind,1) == parents(ind,1) && children(ind,2) == parents(ind,2))
            time_wait_machine(ind,1) = data(ind,5);
        else
            time_wait_robot(ind,1) = data(ind,5);
        end
    end
    
    findEdges;
    figureProps.(['edges' num2str(indexBig)]) = uniqueEdgesWithTimes;
    figureProps.maxEdges(indexBig) = maxPlot;
    figureProps.minEdges(indexBig) = minPlot;
    findNodes;
    figureProps.(['nodes' num2str(indexBig)]) = uniqueNodesWithTimes;
    figureProps.maxNodes(indexBig) = maxPlot;
    figureProps.minNodes(indexBig) = minPlot;
    
    
    data_results = results.(['data' num2str(indexBig)]);
    results.(['resultAverages' num2str(indexBig)]) = mean(data_results);
    results.(['resultStdDev' num2str(indexBig)]) = std(data_results);
end

results.resultAverageFlowTime1 = ...
        sum(figureProps.nodes1(:,end))/results.resultAverages1(1);
results.resultAverageFlowTime2  = ...
        sum(figureProps.nodes2(:,end))/sum(results.resultAverages2([1 3 5]));    
 results.resultAverageFlowTime3 = ...
        sum(figureProps.nodes3(:,end))/results.resultAverages3(1);
    
    

for indexBig = 1:3
    
    %subplot(2,2,indexBig);hold on;
    figure('rend','painters','pos',[10 10 1300 800]);hold on;
    set(gca,'xtick',[])
    set(gca,'xticklabel',[])
    set(gca,'ytick',[])
    set(gca,'yticklabel',[])
    grey = [0.6 0.6 0.6];
    
    scaleMax = 25;
    scaleMin = 1;
    maxPlot = max(figureProps.maxEdges);
    minPlot = min(figureProps.minEdges);
    uniqueEdgesWithTimes = figureProps.(['edges' num2str(indexBig)]);
    plotEdges;
    
    scaleMax = 15;
    scaleMin = 1;
    maxPlot = max(figureProps.maxNodes);
    minPlot = min(figureProps.minNodes);
    uniqueNodesWithTimes = figureProps.(['nodes' num2str(indexBig)]);
    plotNodes;

    xlim([15 145])
    ylim(ylim+[-5 5]);

    saveas(gcf,['CaseStudy' num2str(indexBig) '.png']);
end

sum(figureProps.nodes1(:,end))/135.4
sum(figureProps.nodes2(:,end))/(42.8+40.6+44.6)
sum(figureProps.nodes3(:,end))/134.6
