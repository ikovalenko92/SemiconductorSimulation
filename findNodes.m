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

%maxPlot = max(uniqueNodesWithTimes(:,end))/scaleFactorNode; %scale factor
maxPlot = max(uniqueNodesWithTimes(:,end));
minPlot = min(uniqueNodesWithTimes(:,end));