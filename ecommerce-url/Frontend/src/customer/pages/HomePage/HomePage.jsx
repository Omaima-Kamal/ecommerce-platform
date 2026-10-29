import React, { useEffect, useState } from 'react'
import MainCarousel from '../../components/HomeCarousel/MainCarousel'
import { HomeSectionCarousel } from '../../components/HomeSetionCarousel/HomeSectionCarousel'
import { api } from '../../../config/apiConfig'

const sections = [
    { key: 'men_suit', title: "Men's Suits", topLevelCategory: 'men', secondLevelCategory: 'clothing', category: 'suit' },
    { key: 'women_dresses', title: "Women's Dress", topLevelCategory: 'women', secondLevelCategory: 'clothing', category: 'dresses' },
    { key: 'men_sweaters', title: "Men's Sweater", topLevelCategory: 'men', secondLevelCategory: 'clothing', category: 'sweaters' },
    { key: 'women_blouses', title: "Women's Blouse", topLevelCategory: 'women', secondLevelCategory: 'clothing', category: 'blouses' },
    { key: 'men_shirts', title: "Men's Shirts", topLevelCategory: 'men', secondLevelCategory: 'clothing', category: 'shirt' },
]

export const HomePage = () => {
    const [sectionData, setSectionData] = useState({})
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        let cancelled = false

        const fetchAllSections = async () => {
            const results = await Promise.all(
                sections.map(async (section) => {
                    try {
                        const params = new URLSearchParams()
                        params.append('topLevelCategory', section.topLevelCategory)
                        params.append('secondLevelCategory', section.secondLevelCategory)
                        params.append('category', section.category)
                        params.append('pageNumber', 0)
                        params.append('pageSize', 10)

                        const { data } = await api.get(`/api/products?${params.toString()}`)
                        return { key: section.key, products: data?.content || [] }
                    } catch (error) {
                        return { key: section.key, products: [] }
                    }
                })
            )

            if (!cancelled) {
                const map = {}
                results.forEach((r) => { map[r.key] = r.products })
                setSectionData(map)
                setLoading(false)
            }
        }

        fetchAllSections()
        return () => { cancelled = true }
    }, [])

    return (
        <div>
            <MainCarousel />

            <div className='space-y-10 py-20 flex flex-col justify-center px-5 lg:px-10'>
                {sections.map((section) => {
                    const products = sectionData[section.key] || []
                    if (!loading && products.length === 0) return null

                    return (
                        <HomeSectionCarousel
                            key={section.key}
                            data={products}
                            sectionName={section.title}
                        />
                    )
                })}
            </div>
        </div>
    )
}

export default HomePage