import './Edge.css'
import PropTypes from 'prop-types';
import React, {useLayoutEffect} from 'react';

function Edge(props) {

    const drawDirectedEdge = (v1, v2, newx, newy, headlen, ctx) => {
        var angle = Math.atan2(v2.y - v1.y, v2.x - v1.x);
          if (v2.x > v1.x && v2.y > v1.y){
            newx = v2.x - Math.sin(Math.PI / 2 - angle) * 25;  
            newy = v2.y - Math.cos(Math.PI / 2 - angle) * 25;  
          } else if (v2.x > v1.x && v2.y < v1.y){
            newx = v2.x - Math.sin(Math.PI / 2 - angle) * 25;
            newy = v2.y + Math.cos(Math.PI / 2 + angle) * 25;  
          } else if (v2.x < v1.x && v2.y < v1.y) {
            newx = v2.x - Math.sin(Math.PI / 2 + angle) * 25;  
            newy = v2.y + Math.cos(Math.PI / 2 + angle) * 25;  
          } else if (v2.x < v1.x && v2.y > v1.y){
            newx = v2.x - Math.sin(Math.PI / 2 - angle) * 25;
            newy = v2.y + Math.cos(Math.PI / 2 + angle) * 25;  
          }
          ctx.moveTo(newx, newy);
          ctx.lineTo(newx - headlen * Math.cos(angle - Math.PI / 6), newy - headlen * Math.sin(angle - Math.PI / 6));
          ctx.moveTo(newx, newy);
          ctx.lineTo(newx - headlen * Math.cos(angle + Math.PI / 6), newy - headlen * Math.sin(angle + Math.PI / 6));
    }

    const drawLoop = (v1, v2, newx, newy, headlen, ctx) => {
        ctx.moveTo(v2.x, v2.y - 25);
        ctx.bezierCurveTo(v2.x - 15, v2.y - 50, v2.x - 30, v2.y - 100, v2.x, v2.y - 100);
        ctx.moveTo(v2.x, v2.y - 100);
        ctx.bezierCurveTo(v2.x + 30, v2.y - 100, v2.x + 15, v2.y - 50, v2.x, v2.y - 25);
        if (props.isDirected){
          const angle = Math.atan2(v2.y - 25 - v2.y + 50, v2.x - v1.x -15);;
          newx = v2.x ;
          newy = v2.y - 25;  
          ctx.lineTo(newx - headlen * Math.cos(angle - Math.PI / 6), newy - headlen * Math.sin(angle - Math.PI / 6));
          ctx.moveTo(newx, newy);
          ctx.lineTo(newx - headlen * Math.cos(angle + Math.PI / 6), newy - headlen * Math.sin(angle + Math.PI / 6));  
        }
    }

    const draw = (ids, idt)=>{
        const canvas = document.getElementById("canvas");
        const ctx = canvas.getContext("2d");
        ctx.lineWidth=4;
        const v1 = props.vertices.find(element => element.index===ids);
        const v2 = props.vertices.find(element => element.index===idt);
        ctx.beginPath();
        var newx = 0, newy = 0;
        var headlen = 25; 
        if (ids === idt) {
            drawLoop(v1, v2, newx, newy, headlen, ctx);
        } else {
            ctx.moveTo(v1.x,v1.y);
            ctx.lineTo(v2.x,v2.y);
            if (props.isDirected){
                drawDirectedEdge(v1, v2, newx, newy, headlen, ctx);
            }
        }
        ctx.stroke();
    };

    useLayoutEffect(() => {
        const canvas=document.getElementById('canvas');
        const ctx = canvas.getContext("2d");
        ctx.clearRect(0,0,canvas.width, canvas.height);
        props.edges.forEach(element=>{
          draw(element.source,element.target, element.data);
        })
      });

    return (
        <div>
            {props.isWeighted &&
                props.edges.map(edge => {
                    const v1 = props.vertices.find(element => element.index===edge.source);
                    const v2 = props.vertices.find(element => element.index===edge.target);
                    const midy = (v2.y + v1.y) / 2;
                    const midx = (v2.x + v1.x) / 2;
                    if (v1.x > v2.x) {
                      const angle = Math.atan2(v1.y-v2.y, v1.x-v2.x) * 180 / Math.PI;
                      return (
                        <div >
                          <input type='text' value={edge.weight} style={{position:"absolute", left:`${midx}px`, top: `${midy + 95}px`, transform:`translate(-50%, -50%) rotate(${angle}deg)`, textAlign: "center", transformOrigin:'center', background: 'none', border: 'none', outline: 'none'}}/>
                        </div>
                      )
                    } else {
                      const angle = Math.atan2(v2.y-v1.y, v2.x-v1.x) * 180 / Math.PI;
                      return (
                        <div >
                          <input type='text' value={edge.weight} style={{position:"absolute", left:`${midx}px`, top: `${midy + 95}px`, transform:`translate(-50%, -50%) rotate(${angle}deg)`, textAlign: "center", transformOrigin:'center', background: 'none', border: 'none', outline: 'none'}}/>
                        </div>)
                    }
                    })}
        </div>
    )
}

Edge.propTypes = {
    isWeighted: PropTypes.bool,
    isDirected: PropTypes.bool,
    vertices: PropTypes.array,
    edges: PropTypes.array
  };

export default Edge;