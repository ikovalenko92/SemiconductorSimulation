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