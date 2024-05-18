import React, { useLayoutEffect, useEffect } from "react";
import VertexButton from "./VertexButton";
import './Researcher.css'
import Vertex from "./Vertex";
import GraphMetadata from "./GraphMetadata"
import { useNavigate } from "react-router-dom";

function Researcher() {
  const [vertices, setVertex] = React.useState([]);
  const [count, updateCount] = React.useState(1);
  const [edges,setEdges]=React.useState([]);
  const [edgeCounter,setEdgeCounter]=React.useState(1);
  const [edgeRemove,setEdgeRemove]=React.useState(false);
  const [edgeCreate,setEdgeCreate]=React.useState(false);
  const [vertexRemove,setVertexRemove]=React.useState(false);
  const [isDirected,setIsDirected]=React.useState(false);
  const [coordinates, updateCoordinates] = React.useState([]);
  const navigate = useNavigate();

  function createVertex(xx, yy) {
    setVertexRemove(false);
    setEdgeRemove(false);
    setEdgeCreate(false);
    const newCount = count + 1
    updateCount(newCount)
    // console.log(xx, yy)
    setVertex([...vertices,
      {id: count, 
       vertex: <Vertex data={count} x={xx - 61} y={yy - 230}/>,
       x: xx, y: yy - 90}]
    )
  }

  function updateButtonCoordinates (id, x, y){    
    setVertex(vertices.map(vertex => {
      return vertex.id === id ? { ...vertex, x, y } : vertex}
      ))
  }

  function createEdge(ids,idt){
    setVertexRemove(false);
    setEdgeRemove(false);
    const newCounter=edgeCounter+1
    setEdgeCounter(newCounter)

    setEdges([...edges,{source: ids,
    target: idt,
    id: edgeCounter
    }])
    // console.log(edges)
  }

  function isEdgeCreate(){
    setEdgeRemove(false);
    setEdgeCreate(true);
    setVertexRemove(false);
  }

  function removeEdge(){
    setEdgeCreate(false);
    setEdgeRemove(true);
    setVertexRemove(false);
  }

  function removeVertex(){
    setVertexRemove(true);
    setEdgeCreate(false);
    setEdgeRemove(false);
  }

  function deleteVertex(id){
    setVertex(vertices.filter(vertex => (vertex.id !== id)))
    setEdges(edges.filter(edge => ((edge.source !== id) & (edge.target !== id))))
  }

  function deleteEdge(ids,idt){
    const newCounter=edgeCounter-1
    setEdgeCounter(newCounter)
    setEdges(edges.filter(edge => !(edge.source === ids & edge.target === idt) && !(edge.source === idt & edge.target === ids)))
    // console.log(edges)
  }

  const draw = (ids, idt)=>{
    const canvas = document.getElementById("canvas");
    const ctx = canvas.getContext("2d");
    ctx.lineWidth=4;
    const v1 = vertices.find(element => element.id===ids);
    const v2 = vertices.find(element => element.id===idt);
    ctx.beginPath();
    var newx = 0, newy = 0;
    var headlen = 25; 
    if (ids === idt) {
      ctx.moveTo(v2.x, v2.y - 25);
      ctx.bezierCurveTo(v2.x - 15, v2.y - 50, v2.x - 30, v2.y - 100, v2.x, v2.y - 100);
      ctx.moveTo(v2.x, v2.y - 100);
      ctx.bezierCurveTo(v2.x + 30, v2.y - 100, v2.x + 15, v2.y - 50, v2.x, v2.y - 25);
      if (isDirected){
        const angle = Math.atan2(v2.y - 25 - v2.y + 50, v2.x - v1.x -15);;
        newx = v2.x ;
        newy = v2.y - 25;  
        ctx.lineTo(newx - headlen * Math.cos(angle - Math.PI / 6), newy - headlen * Math.sin(angle - Math.PI / 6));
        ctx.moveTo(newx, newy);
        ctx.lineTo(newx - headlen * Math.cos(angle + Math.PI / 6), newy - headlen * Math.sin(angle + Math.PI / 6));
      }
      ctx.stroke();
    } else if (isDirected){
      var angle = Math.atan2(v2.y - v1.y, v2.x - v1.x );
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
      ctx.moveTo(v1.x, v1.y);
      ctx.lineTo(v2.x, v2.y);
      ctx.moveTo(newx, newy);
      ctx.lineTo(newx - headlen * Math.cos(angle - Math.PI / 6), newy - headlen * Math.sin(angle - Math.PI / 6));
      ctx.moveTo(newx, newy);
      ctx.lineTo(newx - headlen * Math.cos(angle + Math.PI / 6), newy - headlen * Math.sin(angle + Math.PI / 6));
    } else {
      ctx.moveTo(v1.x,v1.y);
      ctx.lineTo(v2.x,v2.y);
    }
    ctx.stroke();
};

  useEffect(() => {
    const drawVertex = (event) => {
      updateCoordinates([event.clientX, event.clientY]);
    }

    const handleKeyPress = (event) => {
      if (event.key === 'v'){
        createVertex(coordinates[0], coordinates[1]);
      }
    };

    document.addEventListener('mousemove', drawVertex);
    window.addEventListener('keypress', handleKeyPress);

    return (() => {
      document.removeEventListener('mousemove', drawVertex);
      window.removeEventListener('keypress', handleKeyPress);
    })
  });

  useLayoutEffect(() => {
    const canvas=document.getElementById('canvas');
    const ctx = canvas.getContext("2d");
    ctx.clearRect(0,0,canvas.width, canvas.height);
    edges.forEach(element=>{
      draw(element.source,element.target);
    })
  });

  const handle = () => {
    setIsDirected(!isDirected);
  }

  const goToSignInPage = () => {
    navigate('/signIn');
  }

  return (
    <div >
      <div >
        <p className='title'>Graph Researcher</p>
      </div>
      <div className="menu">
        <div ><button className='button' onClick={() => createVertex(200, 200)}> Create vertex</button>
            <VertexButton vertices={vertices} 
                          updateButtonCoordinates={updateButtonCoordinates} 
                          createEdge={createEdge} 
                          edgeRemove={edgeRemove} 
                          edgeCreate={edgeCreate} 
                          deleteEdge={deleteEdge}
                          vertexRemove={vertexRemove}
                          deleteVertex={deleteVertex}
                          draw={draw}
                          edges={edges}
                          coordinates={coordinates}/>
        </div>
        <div style={{paddingTop: "3rem"}}>
          <button className='button' onClick={() => isEdgeCreate()}> Create edge</button>
        </div>
        <div style={{paddingTop: "3rem"}}>
          <button className='button' onClick={() => removeVertex()}> Remove vertex</button>
        </div>
        <div style={{paddingTop: "3rem"}}>
        <button className='button' onClick={() => removeEdge()}> Remove edge</button>
        </div>
        <div style={{paddingTop: "3rem"}}>
            <GraphMetadata setEdgeCreate={setEdgeCreate}/>  
        </div>  
        <div style={{paddingTop: "3rem"}}>
          <div>
            <label> <input type="checkbox" onChange={handle}/>  
                Directed
            </label>
          </div>
        </div>
        <div style={{paddingTop: "3rem"}}>
            <button className='button' onClick={goToSignInPage}> SignIn</button>
        </div>
      </div>        
      <div>
        <canvas className="canvas"
                id="canvas"
                width={window.innerWidth-2}
                height={window.innerHeight-112}>
                Canvas
        </canvas>
      </div>
    </div>
  );
}

export default Researcher;

