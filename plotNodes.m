plotThicknessHold1 = zeros(size(uniqueNodesWithTimes,1),1);
hold on;

%Plot
for i = 1:size(uniqueNodesWithTimes,1)
    p_x = uniqueNodesWithTimes(i,1);
    p_y = uniqueNodesWithTimes(i,2);
    
%     %Wait for the machine
%     if (uniqueNodesWithTimes(i,end-3)>0)
%         plotThicknessHold1 = (scaleMax-scaleMin)/(maxPlot-minPlot)*(uniqueNodesWithTimes(i,end-3)- maxPlot)+scaleMax;
%         rectangle('Position',[p_x-plotThicknessHold1,p_y-plotThicknessHold1/2,...
%             plotThicknessHold1,plotThicknessHold1],'Curvature',[1,1],...
%             'EdgeColor','k','FaceColor','w')
%         
%     end
%     
%     %Wait for the robot
%     if ((uniqueNodesWithTimes(i,end-2))>0)
%      
%         plotThicknessHold2 = (scaleMax-scaleMin)/(maxPlot-minPlot)*((uniqueNodesWithTimes(i,end-2)) - maxPlot)+scaleMax;
%         if (~plotThicknessHold1>0)
%             plotThicknessHold1 = -plotThicknessHold2;
%         end
%         
%         rectangle('Position',[p_x+plotThicknessHold1/2,p_y-plotThicknessHold2/2,...
%             plotThicknessHold2,plotThicknessHold2],'Curvature',[1,1],...
%             'EdgeColor','r','FaceColor','r')
%     end
    
    if ((uniqueNodesWithTimes(i,end-2)+uniqueNodesWithTimes(i,end-3))>0)
     % Plot buffers
        %plotThicknessHold = (uniqueNodesWithTimes(i,end-2)+uniqueNodesWithTimes(i,end-3))/maxPlot;
        plotThicknessHold = (scaleMax-scaleMin)/(maxPlot-minPlot)*((uniqueNodesWithTimes(i,end-2)+uniqueNodesWithTimes(i,end-3)) - maxPlot)+scaleMax;
        rectangle('Position',[p_x-plotThicknessHold/2,p_y-plotThicknessHold/2,...
            plotThicknessHold,plotThicknessHold],'Curvature',[1,1],...
            'EdgeColor','r','FaceColor','r')
    end
    
    % Plot machines
    if(uniqueNodesWithTimes(i,5)~=0)
         %plotThicknessManuf = uniqueNodesWithTimes(i,end)/maxPlot;
         plotThicknessManuf = (scaleMax-scaleMin)/(maxPlot-minPlot)*(uniqueNodesWithTimes(i,end-1) - maxPlot)+scaleMax;
         rectangle('Position',[p_x-plotThicknessManuf/2,p_y-plotThicknessManuf-plotThicknessHold/2-0.7,...
            plotThicknessManuf,plotThicknessManuf],'FaceColor','b')
        
    end
end