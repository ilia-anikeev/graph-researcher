import React, { useLayoutEffect } from "react";
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
  const navigate = useNavigate();

  function createVertex() {
    setVertexRemove(false);
    setEdgeRemove(false);
    setEdgeCreate(false);
    const newCount = count + 1
    updateCount(newCount)
    setVertex([...vertices,
      {id: count, 
       vertex: <Vertex key={count} id={count} data={count} draw={draw} edges={edges}/>,
       x: 0, y: 0}]
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
    console.log(edges)
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
    console.log(id)
    setVertex(vertices.filter(vertex => (vertex.id !== id)))
    setEdges(edges.filter(edge => ((edge.source !== id) & (edge.target !== id))))
    console.log("vertices", vertices)
    console.log("edges", edges)
  }

  function deleteEdge(ids,idt){
    const newCounter=edgeCounter-1
    setEdgeCounter(newCounter)
    console.log(ids, idt)
    setEdges(edges.filter(edge => !(edge.source === ids & edge.target === idt) && !(edge.source === idt & edge.target === ids)))
    console.log(edges)
  }

  const draw = (ids, idt)=>{
    const canvas = document.getElementById("canvas");
    const ctx = canvas.getContext("2d");
    ctx.lineWidth=4;
    const v1 = vertices.find(element => element.id===ids);
    const v2 = vertices.find(element => element.id===idt);
    // const v = document.getElementsByClassName('vertex');
    // const a = v.find(element => element.id===ids)

    ctx.beginPath();
    if (isDirected){
      var headlen = 10;
      var angle = Math.atan2(v2.y-v1.y,v2.x-v1.x);
      ctx.beginPath();
      ctx.moveTo(v1.x, v1.y);
      ctx.lineTo(v2.x, v2.y);
      ctx.stroke();
      ctx.beginPath();
      ctx.moveTo(v2.x, v2.y);
      ctx.lineTo(v2.x-headlen*Math.cos(angle-Math.PI/7), v2.y-headlen*Math.sin(angle-Math.PI/7));
      ctx.lineTo(v2.x-headlen*Math.cos(angle+Math.PI/7), v2.y-headlen*Math.sin(angle+Math.PI/7));
      ctx.lineTo(v2.x, v2.y);
      ctx.lineTo(v2.x-headlen*Math.cos(angle-Math.PI/7), v2.y-headlen*Math.sin(angle-Math.PI/7));
      ctx.stroke();
    } else {
      ctx.moveTo(v1.x,v1.y);
      ctx.lineTo(v2.x,v2.y);
      ctx.stroke();
    }
};

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
        <div ><button className='button' onClick={() => createVertex()}> Create vertex</button>
            <VertexButton vertices={vertices} 
                          updateButtonCoordinates={updateButtonCoordinates} 
                          createEdge={createEdge} 
                          edgeRemove={edgeRemove} 
                          edgeCreate={edgeCreate} 
                          deleteEdge={deleteEdge}
                          vertexRemove={vertexRemove}
                          deleteVertex={deleteVertex}
                          draw={draw}
                          edges={edges}/>
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

