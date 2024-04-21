import React, { useRef } from "react";
import PropTypes from 'prop-types';
import "./VertexButton.css";


function VertexButton(props) {
    const verticesRef = useRef([]);
    const [drawEdge,setDrawEdge]=React.useState(false);
    const [source,setSource]=React.useState(0);

    const handleMouseMove = (id, vertex) => (event) => {
        const rect = event.target.getBoundingClientRect();
        props.updateButtonCoordinates(id, rect.x+25, rect.y-100);
        createEdge(id);    
        deleteEdge(id);
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
    edgeCreate: PropTypes.bool,
    source: PropTypes.number,
    createEdge: PropTypes.func
}

export default VertexButton;

