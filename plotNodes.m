combine_node = [parents time_stay_parent];
combine_node = [round(combine_node(:,1:2),-1) combine_node(:,3)];

% Find the unique edges
[uniqueNodes, ~, ib] = unique(combine_node(:,1:2), 'rows');

% Append hold time, manufacturing time, and total time
uniqueNodesWithTimes = [uniqueNodes zeros(size(uniqueNodes,1),3)];
for ind = 1:size(uniqueNodes,1)
   %append hold time
   indsHold = find(all(uniqueNodes(ind,:)==combine_node(:,1:2),2));
   uniqueNodesWithTimes(ind,end-2) = sum(combine_node(indsHold,end));
   
   %append manufacturing time
   indsManuf = find(all(uniqueNodes(ind,:)==nodesManufacturing(:,1:2),2));
   uniqueNodesWithTimes(ind,end-1) = sum(nodesManufacturing(indsManuf,end));
   
   %append total time
   uniqueNodesWithTimes(ind,end) = sum(uniqueNodesWithTimes(ind,end-2:end-1));
end

plotThicknessHold = zeros(size(uniqueNodesWithTimes,1),1);
hold on;

%Plot
maxPlot = max(uniqueNodesWithTimes(:,end))/scaleFactorNode; %scale factor
for i = 1:size(uniqueNodesWithTimes,1)
    p_x = uniqueNodesWithTimes(i,1);
    p_y = uniqueNodesWithTimes(i,2);
    
    % Plot buffers
    if(uniqueNodesWithTimes(i,4)==0)
        plotThicknessHold = uniqueNodesWithTimes(i,end)/maxPlot+1;
        rectangle('Position',[p_x-plotThicknessHold,p_y-plotThicknessHold,...
            2*plotThicknessHold,2*plotThicknessHold],'Curvature',[1,1],...
            'EdgeColor','r','FaceColor','r')
     % Plot machines
     else
         plotThicknessManuf = uniqueNodesWithTimes(i,end)/maxPlot+1;
         rectangle('Position',[p_x-plotThicknessManuf,p_y-plotThicknessManuf,...
            2*plotThicknessManuf,2*plotThicknessManuf],'FaceColor','b')
        
        plotThicknessHold = uniqueNodesWithTimes(i,end-2)/maxPlot+1;
         rectangle('Position',[p_x-plotThicknessHold,p_y-plotThicknessHold,...
            2*plotThicknessHold,2*plotThicknessHold],'Curvature',[1,1],...
            'EdgeColor','r','FaceColor','r')
    end
end