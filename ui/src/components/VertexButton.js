import React, { useRef } from "react";
import PropTypes from 'prop-types';
import "./VertexButton.css";


function VertexButton(props) {
    const verticesRef = useRef([]);
    const [drawEdge,setDrawEdge]=React.useState(false);
    const [source,setSource]=React.useState(0);

    const handleMouseMove = (id, vertex) => (event) => {
        const rect = event.target.getBoundingClientRect();
        const v = document.getElementsByClassName('vertex');
        const a = Array.prototype.find.call(v, ver => ((rect.y <= ver.getBoundingClientRect().bottom) && (rect.y >= ver.getBoundingClientRect().top) & (rect.x <= ver.getBoundingClientRect().right) && (rect.x >= ver.getBoundingClientRect().left)))
        props.updateButtonCoordinates(id, a.getBoundingClientRect().left + (a.getBoundingClientRect().right - a.getBoundingClientRect().left) / 2, a.getBoundingClientRect().top + (a.getBoundingClientRect().top - a.getBoundingClientRect().bottom) / 2 - 50);
        console.log(a.getBoundingClientRect().left + (a.getBoundingClientRect().right - a.getBoundingClientRect().left) / 2, a.getBoundingClientRect().top + (a.getBoundingClientRect().top - a.getBoundingClientRect().bottom) / 2 - 100)
        createEdge(id);    
        deleteEdge(id);
        deleteVertex(id)
    };

    function createEdge(id){
        if (props.edgeCreate === true){
            if(drawEdge===true){
                if(id===props.source){
                    setDrawEdge(false);
                    return;
                }else{
                   props.createEdge(source,id);
                   setDrawEdge(false);
                }
            }else{
                setSource(id);
                setDrawEdge(true);
            }
        } 
    }

    function deleteEdge(id){
        if (props.edgeRemove === true){
            if(drawEdge===true){
                if(id===props.source){
                    setDrawEdge(false);
                    return;
                }else{
                    props.deleteEdge(source,id);
                    setDrawEdge(false);
                }
            }else{
                setSource(id);
                setDrawEdge(true);
            }
        }
    }
    
    function deleteVertex(id){
        if (props.vertexRemove === true){
            props.deleteVertex(id);
        }
    }

    return (
        <div>
            {props.vertices ? props.vertices.map(vertex => {
                return (
                    <div 
                        key={vertex.id} 
                        ref={ref => verticesRef.current[vertex.id] = ref}
                        onClick={handleMouseMove(vertex.id, vertex.vertex)}
                    >
                        {vertex.vertex}
                    </div>
                );
            }) : null}  
        </div>
    );
}

VertexButton.propTypes = {
    vertices: PropTypes.array,
    updateButtonCoordinates: PropTypes.func,
    deleteEdge: PropTypes.func,
    edgeRemove: PropTypes.bool,
    vertexRemove: PropTypes.bool,
    deleteVetrex: PropTypes.func,
    edgeCreate: PropTypes.bool,
    source: PropTypes.number,
    createEdge: PropTypes.func
}

export default VertexButton;

