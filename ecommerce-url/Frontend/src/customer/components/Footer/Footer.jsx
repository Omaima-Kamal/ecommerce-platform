import { Link } from '@mui/material'
import React from 'react'

const footerSections = [
    {
        title: 'Company',
        links: ['About', 'Blog', 'Press', 'Jobs', 'Partners'],
    },
    {
        title: 'Solutions',
        links: ['Marketing', 'Analytics', 'Commerce', 'Insights', 'Support'],
    },
    {
        title: 'Documentation',
        links: ['Guides', 'API Status'],
    },
    {
        title: 'Legal',
        links: ['Claim', 'Privacy', 'Terms'],
    },
];

export const Footer = () => {
    return (
        <div className='bg-black text-white'>
            <div className='flex flex-wrap justify-between px-6 md:px-16 py-12 gap-8'>
                {footerSections.map((section) => (
                    <div key={section.title} className='min-w-[120px]'>
                        <h3 className='text-lg font-medium mb-4'>{section.title}</h3>
                        <div className='flex flex-col gap-3'>
                            {section.links.map((link) => (
                                <Link
                                    key={link}
                                    href="#"
                                    color="inherit"
                                    underline="hover"
                                    className='!text-sm !opacity-80 hover:!opacity-100'
                                >
                                    {link}
                                </Link>
                            ))}
                        </div>
                    </div>
                ))}
            </div>

            <div className='text-center pb-8 text-sm'>
                <p>&copy; 2026 My Company. All rights reserved.</p>
                <p>Made with love by Me.</p>
                <p>
                    Icons made by{' '}
                    <Link href="https://www.freepik.com" color="inherit" underline="always">
                        Freepik
                    </Link>{' '}
                    from{' '}
                    <Link href="https://www.flaticon.com" color="inherit" underline="always">
                        www.flaticon.com
                    </Link>
                </p>
            </div>
        </div>
    )
}