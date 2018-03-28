plotThickness = zeros(size(uniqueEdgesWithTimes,1),1);

%Plot
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
            %plotThickness = uniqueEdgesWithTimes(i,end)/maxPlot;
            plotThickness = (scaleMax-scaleMin)/(maxPlot-minPlot)*(uniqueEdgesWithTimes(i,end) - maxPlot)+scaleMax;
            plot([p_x_rounded c_x_rounded],[p_y_rounded c_y_rounded],...
                'LineWidth', plotThickness,'color',grey);
        end
    else
        nodesManufacturing = [nodesManufacturing;p_x p_y uniqueEdgesWithTimes(i,end)];
    end
end
