combine_node = [parents time_wait_machine time_wait_robot];
combine_node = [round(combine_node(:,1:2),-1) combine_node(:,3:end)];

% Find the unique edges
[uniqueNodes, ~, ib] = unique(combine_node(:,1:2), 'rows');

% Append hold time, manufacturing time, and total time
uniqueNodesWithTimes = [uniqueNodes zeros(size(uniqueNodes,1),4)];
for ind = 1:size(uniqueNodes,1)
   %append hold at machine time
   indsHold = find(all(uniqueNodes(ind,:)==combine_node(:,1:2),2));
   uniqueNodesWithTimes(ind,end-3) = sum(combine_node(indsHold,end-1));
    
   %append hold at robot time
   uniqueNodesWithTimes(ind,end-2) = sum(combine_node(indsHold,end-2));
   
   %append manufacturing time
   indsManuf = find(all(uniqueNodes(ind,:)==nodesManufacturing(:,1:2),2));
   uniqueNodesWithTimes(ind,end-1) = sum(nodesManufacturing(indsManuf,end));
   
   %append total time
   uniqueNodesWithTimes(ind,end) = sum(uniqueNodesWithTimes(ind,end-3:end-1));
end

plotThicknessHold = zeros(size(uniqueNodesWithTimes,1),1);
hold on;

%Plot
%maxPlot = max(uniqueNodesWithTimes(:,end))/scaleFactorNode; %scale factor
maxPlot = max(uniqueNodesWithTimes(:,end));
minPlot = min(uniqueNodesWithTimes(:,end));

for i = 1:size(uniqueNodesWithTimes,1)
    p_x = uniqueNodesWithTimes(i,1);
    p_y = uniqueNodesWithTimes(i,2);
    
    % Plot machines
    if(uniqueNodesWithTimes(i,5)~=0)
         %plotThicknessManuf = uniqueNodesWithTimes(i,end)/maxPlot;
         plotThicknessManuf = (scaleMax-scaleMin)/(maxPlot-minPlot)*(uniqueNodesWithTimes(i,end) - maxPlot)+scaleMax;
         rectangle('Position',[p_x-plotThicknessManuf/2,p_y-plotThicknessManuf/2,...
            plotThicknessManuf,plotThicknessManuf],'FaceColor','b')
        
    end
    
    if ((uniqueNodesWithTimes(i,end-2)+uniqueNodesWithTimes(i,end-3))>0)
    
     % Plot buffers
        %plotThicknessHold = (uniqueNodesWithTimes(i,end-2)+uniqueNodesWithTimes(i,end-3))/maxPlot;
        plotThicknessHold = (scaleMax-scaleMin)/(maxPlot-minPlot)*((uniqueNodesWithTimes(i,end-2)+uniqueNodesWithTimes(i,end-3)) - maxPlot)+scaleMax;
        rectangle('Position',[p_x-plotThicknessHold/2,p_y-plotThicknessHold/2,...
            plotThicknessHold,plotThicknessHold],'Curvature',[1,1],...
            'EdgeColor','r','FaceColor','r')
    end
    
    %Wait for the machine
    if (uniqueNodesWithTimes(i,end-3)>0)
       % plotThicknessHold = uniqueNodesWithTimes(i,end-3)/maxPlot;
        plotThicknessHold = (scaleMax-scaleMin)/(maxPlot-minPlot)*(uniqueNodesWithTimes(i,end-3)- maxPlot)+scaleMax;
        rectangle('Position',[p_x-plotThicknessHold/2,p_y-plotThicknessHold/2,...
            plotThicknessHold,plotThicknessHold],'Curvature',[1,1],...
            'EdgeColor','w','FaceColor','w')
        
    end
end