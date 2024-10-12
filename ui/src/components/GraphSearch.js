import React, { useState ,useContext} from 'react';
import './GraphSearch.css';
import {useNavigate} from 'react-router-dom';
import {AppContext} from './../AppContext';

function GraphSearch() {
    const [formData, setFormData] = useState({
        is_connected: false,
        is_biconnected: false,
        is_planar: false,
        is_chordal: false,
        chromatic_number: 0,
        is_bipartite: false,
    });

    const [graphs, setGraphs] = useState([]);
    const [page, setPage] = useState(1);
    const navigate = useNavigate();
    const {setGraphId} = useContext(AppContext);
    const {setIsSearched} =useContext(AppContext);
    const handleInputChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData({
            ...formData,
            [name]: type === 'checkbox' ? checked : value,
        });
    };
    const handleGraphClick = (searchId) =>{
        setGraphId(searchId);
        setIsSearched(true);
        navigate('/');
    }
    const fetchGraphs = (pageNumber) => {
        console.log("Отправляю Json: ", JSON.stringify(formData));
        fetch(`http://localhost:8080/search-graphs?page=${pageNumber}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json','Connection': 'keep-alive' },
            body: JSON.stringify(formData),
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text) });
            }
            return response.json();
        })
        
        .then(data => {
            if (pageNumber === 1) {
                setGraphs(data.graphs);
            } else {
                setGraphs(prevGraphs => [...prevGraphs, ...data.graphs]);
            }
            setPage(pageNumber);
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        fetchGraphs(1); 
    };

    const loadMoreGraphs = () => {
        fetchGraphs(page + 1);
    };

    return (
        <div className="graph-search-container">
            <h1>Search Graphs</h1>
            <form className="graph-search-form" onSubmit={handleSubmit}>
                <label>
                    Is Connected:
                    <input type="checkbox" name="is_connected" onChange={handleInputChange} />
                </label>
                <label>
                    Is Biconnected:
                    <input type="checkbox" name="is_biconnected" onChange={handleInputChange} />
                </label>
                <label>
                    Is Planar:
                    <input type="checkbox" name="is_planar" onChange={handleInputChange} />
                </label>
                <label>
                    Is Chordal:
                    <input type="checkbox" name="is_chordal" onChange={handleInputChange} />
                </label>
                <label>
                    Chromatic Number:
                    <input type="number" name="chromatic_number" onChange={handleInputChange} />
                </label>
                <label>
                    Is Bipartite:
                    <input type="checkbox" name="is_bipartite" onChange={handleInputChange} />
                </label>
                <button type="submit">Search</button>
            </form>

            <div className="graph-list">
                {graphs.map((graph, index) => (
                    <div key={index} onClick={()=>handleGraphClick(graph)}>Graph ID: {graph}</div>
                ))}
            </div>

            <button className="load-more-button" onClick={loadMoreGraphs}>Load More</button>
        </div>
    );
}

export default GraphSearch;