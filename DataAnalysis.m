close all;clear all;

st.data1 = [];
st.data2 = [];
st.data3 = [];
%st.data4 = [];

results.data1 = [];
results.data2 = [];
results.data3 = [];
%results.data4 = [];

for testIndex = 1:5
    st.data1 = [st.data1; csvread(['T' num2str(testIndex) 'outFile' num2str(1) '.txt'])];
    st.data2 = [st.data2; csvread(['T' num2str(testIndex) 'outFile' num2str(2) '.txt'])];
    st.data3 = [st.data3; csvread(['T' num2str(testIndex) 'outFile' num2str(3) '.txt'])];
    %st.data4 = [st.data4; csvread(['T' num2str(testIndex) 'outFile' num2str(4) '.txt'])];
    
    results1 = readtable(['T' num2str(testIndex) 'outFile50000.0.txt']);
    results2 = readtable(['T' num2str(testIndex) 'outFile100000.0.txt']);
    results3 = readtable(['T' num2str(testIndex) 'outFile150000.0.txt']);
    %results4 = readtable(['T' num2str(testIndex) 'outFile250000.0.txt']);

    results.data1 = [results.data1; results1.Var5'];
    results.data3 = [results.data3; results3.Var5'];
    %results.data4 = [results.data4; results4.Var5'];
    
    if(size(results2.Var5,1)==4)
       results.data2 = [results.data2; [results2.Var5(1:end-1)' 0 results2.Var5(end)' 0]];
    elseif (results2.Var5(4,1) >25)
       results.data2 = [results.data2; [results2.Var5(1:end-2)' 0 results2.Var5(end-1:end)']];
    else
       results.data2 = [results.data2; results2.Var5'];
    end
end

for indexBig = 1:3
    clearvars -except indexBig st results;
    data = st.(['data' num2str(indexBig)]);
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
    
    %subplot(2,2,indexBig);hold on;
    figure('rend','painters','pos',[10 10 1300 800]);hold on;
    set(gca,'xtick',[])
    set(gca,'xticklabel',[])
    set(gca,'ytick',[])
    set(gca,'yticklabel',[])
    grey = [0.6 0.6 0.6];
    scaleMax = 50;
    scaleMin = 2;
    plotEdges;
    scaleMax = 8;
    scaleMin = 0.5;
    plotNodes;

    xlim([15 145])
    ylim(ylim+[-5 5]);
    
    data_results = results.(['data' num2str(indexBig)]);
    results.(['resultAverages' num2str(indexBig)]) = mean(data_results);
    results.(['resultStdDev' num2str(indexBig)]) = std(data_results);
    
    saveas(gcf,['CaseStudy' num2str(indexBig) '.png']);
end