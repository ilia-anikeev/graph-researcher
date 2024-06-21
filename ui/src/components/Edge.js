import './Edge.css';
import PropTypes from 'prop-types';
import React, {useEffect, useState} from 'react';

function Edge(props) {
    const [weight, setWeight] = useState(props.weight.toString());

    const drawDirectedEdge = (source, target, newX, newY, headlen, ctx) => {
        var angle = Math.atan2(target.y - source.y, target.x - source.x);
        if (target.x > source.x && target.y > source.y){
            newX = target.x - Math.sin(Math.PI / 2 - angle) * 25;
            newY = target.y - Math.cos(Math.PI / 2 - angle) * 25;
        } else if (target.x > source.x && target.y < source.y){
            newX = target.x - Math.sin(Math.PI / 2 - angle) * 25;
            newY = target.y + Math.cos(Math.PI / 2 + angle) * 25;
        } else if (target.x < source.x && target.y < source.y) {
            newX = target.x - Math.sin(Math.PI / 2 + angle) * 25;
            newY = target.y + Math.cos(Math.PI / 2 + angle) * 25;
        } else if (target.x < source.x && target.y > source.y){
            newX = target.x - Math.sin(Math.PI / 2 - angle) * 25;
            newY = target.y + Math.cos(Math.PI / 2 + angle) * 25;
        }
        ctx.moveTo(newX, newY);
        ctx.lineTo(newX - headlen * Math.cos(angle - Math.PI / 6), newY - headlen * Math.sin(angle - Math.PI / 6));
        ctx.moveTo(newX, newY);
        ctx.lineTo(newX - headlen * Math.cos(angle + Math.PI / 6), newY - headlen * Math.sin(angle + Math.PI / 6));
    }

    const drawLoop = (source, target, newX, newY, headlen, ctx) => {
        ctx.moveTo(target.x, target.y - 25);
        ctx.bezierCurveTo(target.x - 15, target.y - 50, target.x - 30, target.y - 100, target.x, target.y - 100);
        ctx.moveTo(target.x, target.y - 100);
        ctx.bezierCurveTo(target.x + 30, target.y - 100, target.x + 15, target.y - 50, target.x, target.y - 25);
        if (props.isDirected){
            const angle = Math.atan2(target.y - 25 - target.y + 50, target.x - source.x -15);
            newX = target.x;
            newY = target.y - 25;
            ctx.lineTo(newX - headlen * Math.cos(angle - Math.PI / 6), newY - headlen * Math.sin(angle - Math.PI / 6));
            ctx.moveTo(newX, newY);
            ctx.lineTo(newX - headlen * Math.cos(angle + Math.PI / 6), newY - headlen * Math.sin(angle + Math.PI / 6));
        }
    }

    const draw = (idSource, idTarget) => {
        const canvas = document.getElementById("canvas");
        const ctx = canvas.getContext("2d");
        ctx.lineWidth = 4;
        const source = props.vertices.find(vertex => vertex.index === idSource);
        const target = props.vertices.find(vertex => vertex.index === idTarget);
        ctx.beginPath();
        var newX = 0, newY = 0;
        var headlen = 25; 
        if (idSource === idTarget) {
            drawLoop(source, target, newX, newY, headlen, ctx);
        } else {
            ctx.moveTo(source.x, source.y);
            ctx.lineTo(target.x, target.y);
            if (props.isDirected){
                drawDirectedEdge(source, target, newX, newY, headlen, ctx);
            }
        }
        ctx.stroke();
    };

    useEffect(() => {
        const canvas = document.getElementById('canvas');
        const ctx = canvas.getContext("2d");
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        props.edges.forEach(edge => {
            draw(edge.source, edge.target);
        })
      });

    const handleWeightChange = (e) => {
        const newWeight = e.target.value;
        props.addEdge(props.edges.map(edge =>{
            return ((edge.source === props.source & edge.target === props.target 
                  || edge.source === props.target & edge.target === props.source)) ? { ...edge, weight: parseInt(newWeight) } : edge;
        }
        ));
        setWeight(newWeight);
}

    const setActualWeight = (e) => {
        setWeight(e.target.value);
    }


    return (
        <div className='weightedEdge'>
            {props.isWeighted && (() => { 
                    const source = props.vertices.find(vertex => vertex.index === props.source);
                    const target = props.vertices.find(vertex => vertex.index === props.target);
                    const midX = (target.x + source.x) / 2;
                    const midY = (target.y + source.y) / 2;
                    if (source.x > target.x) {
                      const angle = Math.atan2(source.y - target.y, source.x - target.x) * 180 / Math.PI;
                      return (
                        <div >
                          <input type='text' value={weight} style={{position:"absolute", left:`${midX}px`, top: `${midY + 95}px`,
                                 transform:`translate(-50%, -50%) rotate(${angle}deg)`, textAlign: "center", transformOrigin:'center',
                                 background: 'none', border: 'none', outline: 'none'}}
                                 onChange={setActualWeight}
                                 onBlur={handleWeightChange} />
                        </div>
                      );
                    } else {
                      const angle = Math.atan2(target.y - source.y, target.x - source.x) * 180 / Math.PI;
                      return (
                        <div >
                          <input type='text' value={weight} style={{position:"absolute", left:`${midX}px`, top: `${midY + 95}px`,
                                 transform:`translate(-50%, -50%) rotate(${angle}deg)`, textAlign: "center", transformOrigin:'center',
                                 background: 'none', border: 'none', outline: 'none'}}
                                 onChange={setActualWeight}
                                 onBlur={handleWeightChange}/>
                        </div>);
                    }
                })()
                }
        </div>
    )
}

Edge.propTypes = {
    isWeighted: PropTypes.bool,
    isDirected: PropTypes.bool,
    vertices: PropTypes.array,
    addEdge: PropTypes.func,
    edges: PropTypes.array,
    weight: PropTypes.number,
    source: PropTypes.number,
    target: PropTypes.number,
  };

export default Edge;