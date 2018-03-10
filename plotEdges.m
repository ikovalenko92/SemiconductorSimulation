combine_edge = [parents children time_move];

% Edge direction doesn't matter
%(i.e. parent to child edge = child to parent edge
for i = 1:size(combine_edge,1)
    if (combine_edge(i,3)>combine_edge(i,1))
        temp = combine_edge(i,1);
        combine_edge(i,1) = combine_edge(i,3);
        combine_edge(i,3) = temp;
        
        temp2 = combine_edge(i,2);
        combine_edge(i,2) = combine_edge(i,4);
        combine_edge(i,4) = temp2;
    end
end

% Find the unique edges
[uniqueEdges, ~, ib] = unique(combine_edge(:,1:4), 'rows');  
uniqueEdgesWithTimes = [uniqueEdges zeros(size(uniqueEdges,1),1)];

%For each edge, add up how long they took
for ind = 1:size(uniqueEdges,1)
   inds = find(all(uniqueEdges(ind,:)==combine_edge(:,1:4),2));
   uniqueEdgesWithTimes(ind,end) = sum(combine_edge(inds,end));
end

plotThickness = zeros(size(uniqueEdgesWithTimes,1),1);

%Plot
maxPlot = max(uniqueEdgesWithTimes(:,end))/scaleFactorEdge; %scale factor
nodesManufacturing = [];
for i = 1:size(uniqueEdgesWithTimes,1)
    p_x = uniqueEdgesWithTimes(i,1);
    p_y = uniqueEdgesWithTimes(i,2);
    c_x = uniqueEdgesWithTimes(i,3);
    c_y = uniqueEdgesWithTimes(i,4);
    
    % Save edges that are loops separetly (manufacturing edges)
    if(~(p_x == c_x && p_y == c_y))
        %Try bypassing the buffer edges 
        p_x_rounded = round(p_x,-1);
        p_y_rounded = round(p_y,-1);
        c_x_rounded = round(c_x,-1);
        c_y_rounded = round(c_y,-1);
        
        % Don't plot buffer lines
        if (~(p_x_rounded == c_x_rounded && p_y_rounded == c_y_rounded))
            plotThickness = uniqueEdgesWithTimes(i,end)/maxPlot;
            plot([p_x_rounded c_x_rounded],[p_y_rounded c_y_rounded],...
                'LineWidth', plotThickness,'color',grey);
        end
    else
        nodesManufacturing = [nodesManufacturing;p_x p_y uniqueEdgesWithTimes(i,end)];
    end
end
